package dev.blackgate.questsystem.util.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface InventoryGUI {
    public Inventory getInventory();
    public void open(Player player);
    public List<ItemStack> getItems();
}
