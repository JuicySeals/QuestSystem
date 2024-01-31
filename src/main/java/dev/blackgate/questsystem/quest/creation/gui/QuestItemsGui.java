package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import dev.blackgate.questsystem.util.interfaces.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestItemsGui implements InventoryGUI {
    private Inventory inventory;
    private ConfigHelper configHelper;
    public QuestItemsGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
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

    private void create() {
        inventory = Bukkit.createInventory(null, 54, ChatColor.stripColor(configHelper.getQuestCreationMessage("place-items")));
        inventory.setItem(53, getItems().get(0));
    }
}