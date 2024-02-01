package dev.blackgate.questsystem.util;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private final Map<Inventory, InventoryGUI> activeInventories;

    public InventoryManager() {
        activeInventories = new HashMap<>();
    }

    public void registerHandledInventory(Inventory inventory, InventoryGUI inventoryHandler) {
        activeInventories.put(inventory, inventoryHandler);
    }

    public void unregisterHandledInventory(Inventory inventory) {
        activeInventories.remove(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        if (activeInventories.get(event.getInventory()) != null) {
            activeInventories.get(event.getInventory()).onClick(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        if (activeInventories.get(event.getInventory()) != null) {
            Inventory inventory = event.getInventory();
            activeInventories.get(inventory).onClose(event);
            unregisterHandledInventory(inventory);
        }
    }
}
