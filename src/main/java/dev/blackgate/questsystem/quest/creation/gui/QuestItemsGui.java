package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.InventoryGUI;
import dev.blackgate.questsystem.util.config.ConfigHelper;
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
import java.util.Iterator;
import java.util.List;

public class QuestItemsGui implements InventoryGUI {
    private final ConfigHelper configHelper;
    private Inventory inventory;
    private boolean isSet;
    private QuestSystem questSystem;

    public QuestItemsGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.isSet = false;
        this.questSystem = questSystem;
        create();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(ChatColor.GREEN + "Finish");
        finish.setItemMeta(finishMeta);
        items.add(finish);
        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)
                || event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Finish")) {
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());
            items = filterItems(items);
            finish((Player) event.getWhoClicked(), items);
        }
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 54, ChatColor.stripColor(configHelper.getQuestCreationMessage("place-items")));
        inventory.setItem(53, getItems().get(0));
    }

    private void finish(Player player, List<ItemStack> items) {
        isSet = true;
        QuestCreator creator = questSystem.getQuestCreationManager().getQuestCreator(player);
        creator.setItems(items);
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("added-items").replace("%value%", String.valueOf(items.size())));
        creator.openQuestRewardPrompt(QuestRewardType.COMMAND);
        player.closeInventory();
    }

    private List<ItemStack> filterItems(List<ItemStack> items) {
        List<ItemStack> updatedItems = new ArrayList<>(items);

        Iterator<ItemStack> itemStackIterator = updatedItems.iterator();
        while (itemStackIterator.hasNext()) {
            ItemStack item = itemStackIterator.next();
            if (item == null) {
                itemStackIterator.remove();
                continue;
            }
            if (item.getType() != Material.BAMBOO || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                continue;
            }
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Finish")) {
                itemStackIterator.remove();
            }
        }
        return updatedItems;
    }
}