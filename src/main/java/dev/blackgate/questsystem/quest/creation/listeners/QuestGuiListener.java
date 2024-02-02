package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class QuestGuiListener implements Listener {
    private QuestSystem questSystem;

    public QuestGuiListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        questSystem.getInventoryManager().handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        questSystem.getInventoryManager().handleClose(event);
    }
}
