package dev.blackgate.questsystem.listeners;

import dev.blackgate.questsystem.Quest;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
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
        if("TEST-PLAYER".equals(event.getPlayer().getName())) return;
        Database database = questSystem.getDatabase();
        if(!database.isPlayerInDatabase(event.getPlayer())) questSystem.getCoinManager().addPlayer(event.getPlayer());
    }
}
