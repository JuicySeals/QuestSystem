package dev.blackgate.questsystem.coin.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.coin.CoinDatabaseManager;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerCoinJoinListener implements Listener {
    private final Database database;
    private final CoinDatabaseManager coinDatabaseManager;

    public PlayerCoinJoinListener(QuestSystem questSystem) {
        this.database = questSystem.getDatabase();
        this.coinDatabaseManager = questSystem.getCoinManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if ("TEST-PLAYER".equals(player.getName()))
            return; // For unit tests (No possible side effects as real players can't have - in their name.)
        if (database == null) {
            return;
        }
        CompletableFuture<Boolean> completableFuture = coinDatabaseManager.isPlayerInDatabase(player);
        completableFuture.whenComplete(((isInDb, exception) -> {
            if (exception != null) {
                Logger.severe(String.format("Failed to check if %s is in coin database", player.getName()));
                return;
            }
            if (!isInDb) {
                coinDatabaseManager.addPlayer(player);
            }
        }));
    }
}
