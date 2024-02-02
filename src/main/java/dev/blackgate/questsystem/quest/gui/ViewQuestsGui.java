package dev.blackgate.questsystem.quest.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestManager;
import dev.blackgate.questsystem.quest.QuestReward;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewQuestsGui implements InventoryGUI {
    private QuestManager questManager;
    private QuestSystem questSystem;
    private Inventory inventory;
    public ViewQuestsGui(QuestSystem questSystem) {
        this.questManager = questSystem.getQuestManager();
        this.questSystem = questSystem;
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 54, "Quests - Page 1");
        inventory.setContents(getItems().toArray(new ItemStack[0]));
        questSystem.getInventoryManager().registerHandledInventory(getInventory(), this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(getInventory());
    }

    @Override
    public List<ItemStack> getItems() {
        List<Quest> quests = questManager.getQuests();
        List<ItemStack> items = setEdges();
        for(int i = 0; i < quests.size(); i++) {
            int invSlot = getNextEmptySlot(items);
            items.set(invSlot, createItem(quests.get(i)));
        }
        return items;
    }

    private int getNextEmptySlot(List<ItemStack> items) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i) == null) {
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
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Rewards:");
        for(QuestReward reward : quest.getRewards()) {
            lore.add(createMessage(reward));
        }
        lore.add("");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to begin quest!");
        meta.setLore(lore);
        meta.setDisplayName(quest.getQuestName());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private String createMessage(QuestReward questReward) {
        String message = ChatColor.GOLD + "";
        switch (questReward.getRewardType()) {
            case XP -> message += questReward.getXpAmount() + " XP levels";
            case COINS -> message += questReward.getCoinAmount() + " coins";
            case ITEMS -> message += questReward.getItems().size() + " items";
            case COMMAND -> message += questReward.getCommands().size() + " commands";
        }
        return message;
    }

    private List<ItemStack> setEdges() {
        List<ItemStack> itemStacks = Arrays.asList(new ItemStack[54]); // Can't use List#set() without there be an element. So this creates an list filled with null
        int[] edgeSlots = {0,1,2,3,4,5,6,7,8,9,18,27,36,45,46,47,48,49,50,51,52,53,44,35,26,17};
        ItemStack edgeItem = getEdgeItem();
        for(int i = 0; i < edgeSlots.length; i++) {
            itemStacks.set(edgeSlots[i], edgeItem);
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

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
