package dev.blackgate.questsystem.coin;

import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.Player;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CoinManager {
    private final Database database;

    public CoinManager(Database database) {
        this.database = database;
        createCoinTable();
    }

    private void createCoinTable() {
        String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS `coins` (`UUID` VARCHAR(36) NOT NULL,`amount` BIGINT NOT NULL DEFAULT 0,PRIMARY KEY (`UUID`));
                """;

        database.executeStatement(createTableSQL);
    }

    public CompletableFuture<Integer> getCoins(Player player) {
        String query = "SELECT amount FROM coins WHERE UUID = ? LIMIT 1";
        CompletableFuture<CachedRowSet> completableFuture = database.executeQuery(query, List.of(player.getUniqueId().toString()));
        return completableFuture.handleAsync(((rowSet, exception) -> {
            if (exception != null) {
                Logger.printSQLException("Failed to get coins", query, exception);
                return -1;
            }
            try {
                if (rowSet.next()) {
                    return rowSet.getInt("amount");
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            return -1;
        }));
    }

    public CompletableFuture<Void> setCoins(Player player, int amount) {
        return CompletableFuture.runAsync(() -> {
            String statement = "UPDATE coins SET `amount`= ?, `UUID`=?;";
            Object[] variables = {amount, player.getUniqueId().toString()};
            CompletableFuture<Void> completeableFuture = database.executeStatement(statement, Arrays.asList(variables));
            completeableFuture.whenComplete((result, exception) -> {
                if (exception != null) {
                    Logger.printSQLException("Failed to set coins", statement, exception);
                }
            });
        });
    }

    public void resetCoins(Player player) {
        setCoins(player, 0);
    }

    public CompletableFuture<Void> addPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            String insertPlayerSQL = "INSERT INTO `coins` (`UUID`, `amount`) VALUES (?, ?)";
            Object[] variables = {player.getUniqueId().toString(), 0};
            CompletableFuture<Void> completableFuture = database.executeStatement(insertPlayerSQL, List.of(variables));
            completableFuture.whenComplete((result, exception) -> {
                if (exception != null) {
                    Logger.printSQLException("Failed to add player to coins database", insertPlayerSQL, exception);
                }
            });
        });
    }

    public CompletableFuture<Void> removePlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            String removePlayerSQL = "DELETE FROM coins WHERE UUID=?;";
            CompletableFuture<Void> completableFuture = database.executeStatement(removePlayerSQL, List.of(player.getUniqueId().toString()));
            completableFuture.whenComplete((result, exception) -> {
                if (exception != null) {
                    Logger.printSQLException("Failed to remove player to coins database", removePlayerSQL, exception);
                }
            });
        });
    }

    public CompletableFuture<Boolean> isPlayerInDatabase(Player player) {
        String query = "SELECT 1 FROM `coins` WHERE `UUID` = ? LIMIT 1;";

        return database.executeQuery(query, List.of(player.getUniqueId().toString()))
                .thenApplyAsync(rowSet -> {
                    try {
                        return rowSet.next();
                    } catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .exceptionally(exception -> {
                    Logger.printSQLException("Failed to check if " + player.getName() + " is in database", query, exception);
                    return false;
                });
    }
}
