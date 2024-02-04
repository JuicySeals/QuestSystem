package dev.blackgate.questsystem.coin.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.coin.CoinManager;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerJoinListener implements Listener {
    private final Database database;
    private final CoinManager coinManager;

    public PlayerJoinListener(QuestSystem questSystem) {
        this.database = questSystem.getDatabase();
        this.coinManager = questSystem.getCoinManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if ("TEST-PLAYER".equals(player.getName()))
            return; // For unit tests (No possible side effects as real players can't have - in their name.)
        if (database == null) {
            return;
        }
        CompletableFuture<Boolean> completableFuture = coinManager.isPlayerInDatabase(player);
        completableFuture.whenComplete(((isInDb, exception) -> {
            if (exception != null) {
                Logger.severe(String.format("Failed to check if %s is in coin database", player.getName()));
                return;
            }
            if (!isInDb) {
                coinManager.addPlayer(player);
            }
        }));
    }
}
