package dev.blackgate.questsystem.quest.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestManager;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.util.inventory.InventoryGUI;
import dev.blackgate.questsystem.util.inventory.ItemPDC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewQuestsGui implements InventoryGUI {
    private final QuestManager questManager;
    private final QuestSystem questSystem;
    private Inventory inventory;
    private List<Quest> quests;
    private ItemPDC itemPDC;
    private static final String MY_QUESTS = "MY_QUESTS";

    public ViewQuestsGui(QuestSystem questSystem) {
        this.questManager = questSystem.getQuestManager();
        this.questSystem = questSystem;
        this.itemPDC = questSystem.getItemPDC();
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 54, "Quests");
        inventory.setContents(getItems().toArray(new ItemStack[0]));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        Inventory inventoryPlayerCopy = getInventory(); // Inventory copy
        inventoryPlayerCopy.setItem(49, createMyQuestsItem(player));
        if(player.hasPermission("*") || player.isOp()) {
            questSystem.getInventoryManager().registerHandledInventory(inventoryPlayerCopy, this);
            player.openInventory(inventoryPlayerCopy);
            return;
        }
        for (Quest quest : quests) {
            if (!player.hasPermission(quest.getPermission())) {
                int indexToRemove = getQuestSlot(inventoryPlayerCopy, quest);
                if (indexToRemove != -1) {
                    inventoryPlayerCopy.setItem(indexToRemove, null);
                }
            }
        }
        questSystem.getInventoryManager().registerHandledInventory(inventoryPlayerCopy, this); // Have to register as it wont equal as items changed
        player.openInventory(inventoryPlayerCopy);
    }

    private int getQuestSlot(Inventory inventory, Quest quest) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (itemPDC.isItem(item, quest.getQuestName())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<ItemStack> getItems() {
        quests = questManager.getQuests();
        List<ItemStack> items = setEdges();
        for (Quest quest : quests) {
            int invSlot = getNextEmptySlot(items);
            items.set(invSlot, createItem(quest));
        }
        return items;
    }

    private ItemStack createMyQuestsItem(Player player) {
        ItemStack myQuestsItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) myQuestsItem.getItemMeta();
        itemMeta.setOwnerProfile(player.getPlayerProfile());
        itemMeta.setDisplayName(ChatColor.GREEN + "My quests");
        itemMeta.setLore(List.of("", ChatColor.YELLOW + "Click to view your quests"));
        myQuestsItem.setItemMeta(itemMeta);
        itemPDC.set(myQuestsItem, MY_QUESTS);
        return myQuestsItem;
    }

    private int getNextEmptySlot(List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private ItemStack createItem(Quest quest) {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + quest.getDescription());
        lore.add(" ");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Rewards:"); // Cant apply 2 chatcolors together
        for (QuestReward reward : quest.getRewards()) {
            lore.add(createMessage(reward));
        }
        lore.add("");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to begin quest!");
        meta.setLore(lore);
        meta.setDisplayName(quest.getQuestName());
        itemStack.setItemMeta(meta);
        itemPDC.set(itemStack, quest.getQuestName()); // No uppercase like the rest to make quest retrieval easier
        return itemStack;
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

    private List<ItemStack> setEdges() {
        List<ItemStack> itemStacks = new ArrayList<>();
        int[] edgeSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26, 17};
        ItemStack edgeItem = getEdgeItem();
        for (int i = 0; i < 54; i++) {
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

    private Quest getQuest(ItemStack itemStack) {
        return questSystem.getQuestManager().getQuest(itemPDC.getValue(itemStack));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if(item == null) return;
        if(itemPDC.isItem(item, MY_QUESTS)) {
            ViewInProgressQuestsGui viewInProgressQuests = new ViewInProgressQuestsGui(questSystem);
            viewInProgressQuests.open(player);
            return;
        }
        Quest quest = getQuest(event.getCurrentItem());
        if(quest == null) return;
        questSystem.getProgressionManager().addPlayer(player, quest);
        player.closeInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        questSystem.getInventoryManager().unregisterHandledInventory(event.getInventory());
    }
}
