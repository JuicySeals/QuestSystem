package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
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

public class QuestRewardTypeGui implements InventoryGUI {
    private Inventory inventory;
    private ConfigHelper configHelper;
    public QuestRewardTypeGui(QuestSystem questSystem) {
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
        ItemStack command = new ItemStack(Material.COMMAND_BLOCK);
        ItemStack item = new ItemStack(Material.NETHERITE_INGOT);
        ItemStack coins = new ItemStack(Material.GOLD_INGOT);
        ItemStack xp = new ItemStack(Material.EXPERIENCE_BOTTLE);
        items.add(command);
        items.add(item);
        items.add(coins);
        items.add(xp);
        for(int i = 0; i < 4; i++) {
            String name = Formatter.formatEnumName(QuestRewardType.values()[i]);
            ItemMeta meta = items.get(i).getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            items.get(i).setItemMeta(meta);
        }
        return items;
    }

    private void create() {
        String message = configHelper.getQuestCreationMessage("select-type").replace("%stage%", "reward type");
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.stripColor(message));
        List<ItemStack> items = getItems();
        for(int i = 0; i < items.size(); i++) {
            inv.setItem(i+2, items.get(i));
        }
    }
}
