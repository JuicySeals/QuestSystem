package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.gui.QuestItemsGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class QuestItemsGuiListener implements Listener {
    private QuestSystem questSystem;
    public QuestItemsGuiListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Finish")) {
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());
            questSystem.getQuestCreationManager().getQuestCreator((Player) event.getWhoClicked()).setItems(items);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        String itemsGuiTitle = questSystem.getConfigHelper().getQuestCreationMessage("place-items");
        if(title.equals(itemsGuiTitle)) {
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());
            questSystem.getQuestCreationManager().getQuestCreator((Player) event.getPlayer()).setItems(items);
        }
    }
}
