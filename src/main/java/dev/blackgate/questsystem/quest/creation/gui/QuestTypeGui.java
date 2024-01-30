package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Formatter;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import dev.blackgate.questsystem.util.interfaces.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestTypeGui implements InventoryGUI {
    Inventory inventory;
    ConfigHelper configHelper;
    public QuestTypeGui(QuestSystem questSystem) {
        configHelper = questSystem.getConfigHelper();
        create();
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(getInventory());
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, ChatColor.stripColor(configHelper.getQuestCreationMessage("select-type").replace("%stage%", "type")));
        List<ItemStack> items = getItems();
        for(int i = 0; i < items.size(); i++) {
            inventory.setItem(i+2, items.get(i));
        }
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        ItemStack breakBlocks = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemStack killEntities = new ItemStack(Material.NETHERITE_SWORD);
        ItemStack placeBlocks = new ItemStack(Material.OAK_LOG);
        ItemStack obtainItems = new ItemStack(Material.NETHERITE_INGOT);
        ItemStack getAchievment = new ItemStack(Material.EXPERIENCE_BOTTLE);
        items.add(breakBlocks);
        items.add(killEntities);
        items.add(placeBlocks);
        items.add(obtainItems);
        items.add(getAchievment);
        for(int i = 0; i < 5; i++) {
            String name = Formatter.formatEnumName(QuestType.values()[i]);
            ItemMeta meta = items.get(i).getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            items.get(i).setItemMeta(meta);
        }
        return items;
    }
}
