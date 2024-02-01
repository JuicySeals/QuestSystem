package dev.blackgate.questsystem.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface InventoryGUI {
    Inventory getInventory();

    void open(Player player);

    List<ItemStack> getItems();

    void onClick(InventoryClickEvent event);

    void onClose(InventoryCloseEvent event);
}
