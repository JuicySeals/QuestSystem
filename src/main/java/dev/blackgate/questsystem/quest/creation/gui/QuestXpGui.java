package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
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

public class QuestXpGui implements InventoryGUI {
    private Inventory inventory;
    private ConfigHelper configHelper;
    public QuestXpGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, ChatColor.stripColor(configHelper.getQuestCreationMessage("set-xp")));
        Bukkit.getPlayer("JuicySeals").openInventory(Bukkit.createInventory(null, 9));
        List<ItemStack> items = getItems();
        for(int i = 0; i < items.size(); i++) {
            inventory.setItem(i+2, items.get(i));
        }
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
        List<ItemStack> items = new ArrayList<>();

        ItemStack remove10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.RED + "Remove 10 levels");
        ItemStack remove1 = createButton(Material.STONE_BUTTON, ChatColor.RED + "Remove 1 level");
        ItemStack expBottle = createButton(Material.EXPERIENCE_BOTTLE, ChatColor.GREEN + "0 levels");
        ItemStack add1 = createButton(Material.STONE_BUTTON, ChatColor.GREEN + "Add 1 level");
        ItemStack add10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.GREEN + "Add 10 levels");
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(ChatColor.GREEN + "Finish");
        finish.setItemMeta(finishMeta);

        items.add(remove10);
        items.add(remove1);
        items.add(expBottle);
        items.add(add1);
        items.add(add10);
        items.add(finish);

        return items;
    }

    private ItemStack createButton(Material material, String displayName) {
        ItemStack button = new ItemStack(material);
        ItemMeta buttonMeta = button.getItemMeta();
        buttonMeta.setDisplayName(displayName);
        button.setItemMeta(buttonMeta);
        return button;
    }

}
