package dev.blackgate.questsystem.coin.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    QuestSystem questSystem;

    public PlayerJoinListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if ("TEST-PLAYER".equals(event.getPlayer().getName()))
            return; // For unit tests (No possible side effects as real players can't have - in there name.)
        Database database = questSystem.getDatabase();
        if (database == null) {
            Logger.severe("Failed to add player to database");
            return;
        }
        if (!database.isPlayerInDatabase(event.getPlayer())) questSystem.getCoinManager().addPlayer(event.getPlayer());
    }
}
