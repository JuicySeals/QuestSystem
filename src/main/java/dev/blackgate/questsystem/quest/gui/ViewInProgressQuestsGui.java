package dev.blackgate.questsystem.quest.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ViewInProgressQuestsGui implements InventoryGUI {
    private Inventory inventory;
    private ProgressionDatabaseManager progressionDatabaseManager;
    private QuestSystem questSystem;
    public ViewInProgressQuestsGui(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.progressionDatabaseManager = questSystem.getProgressionManager();
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 27, "Your quests");
        inventory.setContents(getItems().toArray(new ItemStack[0]));
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        Inventory inventoryCopy = inventory;
        CompletableFuture<Quest> completableFuture = progressionDatabaseManager.getPlayerQuest(player);
        BukkitRunnable openInventory = new BukkitRunnable() {
            // Cant open inv async
            @Override
            public void run() {

                player.openInventory(inventoryCopy);
            }
        };
        completableFuture.whenCompleteAsync(((quest, throwable) -> {
            if(throwable != null) {
                Logger.printException("Failed to open my quests inventory", throwable);
                return;
            }
            if(quest != null) {
                inventoryCopy.setItem(13, createQuestItem(quest, player).join()); // Ran async so it only blocks ForkJoinPool thread not main so its fine
            }else {
                inventoryCopy.setItem(13, createNoQuestFoundItem());
            }
            openInventory.runTask(questSystem);
            questSystem.getInventoryManager().registerHandledInventory(inventoryCopy, this);
        }));
    }

    private ItemStack createNoQuestFoundItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "No current quest");
        item.setItemMeta(meta);
        return item;
    }

    private CompletableFuture<ItemStack> createQuestItem(Quest quest, Player player) {
        return CompletableFuture.supplyAsync(() -> {
            ItemStack itemStack = new ItemStack(Material.BOOK);
            ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<>();
            int progress = progressionDatabaseManager.getPlayerQuestProgress(player).join(); // Ran async so it only blocks ForkJoinPool thread not main so its fine
            int progressNeeded = progressionDatabaseManager.getProgressNeeded(quest);

            lore.add(ChatColor.YELLOW + quest.getDescription());
            lore.add("");

            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Rewards:");
            for (QuestReward reward : quest.getRewards()) {
                lore.add(createMessage(reward));
            }

            lore.add("");
            lore.add("");

            lore.add(ChatColor.YELLOW + "Progress: " + progress + "/" + progressNeeded);

            meta.setLore(lore);
            meta.setDisplayName(quest.getQuestName());
            itemStack.setItemMeta(meta);

            return itemStack;
        });
    }


    private String createMessage(QuestReward questReward) {
        String message = ChatColor.GOLD + "";
        switch (questReward.getRewardType()) {
            case XP -> message += questReward.getXpAmount() + " XP levels";
            case COINS -> message += questReward.getCoinAmount() + " coins";
            case ITEMS -> {
                int amount = 0;
                for(ItemStack item : questReward.getItems()) {
                    amount += item.getAmount();
                }
                message += amount + " items";
            }
            case COMMAND -> message += questReward.getCommands().size() + " commands";
        }
        return message;
    }

    @Override
    public List<ItemStack> getItems() {
        return setEdges();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    private List<ItemStack> setEdges() {
        List<ItemStack> itemStacks = new ArrayList<>();
        int[] edgeSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20,21, 22, 23,24, 25, 26, 27};
        ItemStack edgeItem = getEdgeItem();
        for (int i = 0; i < 27; i++) {
            final int currentIndex = i; // Variables used in lamda required final
            itemStacks.add(Arrays.stream(edgeSlots) // More performant way to do it instead of 2 for loops
                    .anyMatch(slot -> slot == currentIndex)
                    ? edgeItem
                    : null);
        }
        return itemStacks;
    }


    private ItemStack getEdgeItem() {
        ItemStack edgeItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta edgesMeta = edgeItem.getItemMeta();
        edgesMeta.setDisplayName(" ");
        edgeItem.setItemMeta(edgesMeta);
        return edgeItem;
    }
}
