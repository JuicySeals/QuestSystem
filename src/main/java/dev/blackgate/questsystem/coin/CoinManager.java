package dev.blackgate.questsystem.coin;

import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.UUIDConverter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CoinManager {
    private final Database database;

    public CoinManager(Database database) {
        this.database = database;
    }

    public int getCoins(Player player) {
        String query = "SELECT amount from coins WHERE UUID = ?";
        AtomicInteger resultHolder = new AtomicInteger(-1);
        database.executeQuery(query,
                resultSet -> {
                    try {
                        if (resultSet.next()) {
                            resultHolder.set(resultSet.getInt("amount"));
                        }
                    } catch (SQLException e) {
                        Logger.severe("Failed to get coins for " + player.getName());
                        e.printStackTrace();
                    }
                }, List.of(UUIDConverter.toByteArray(player.getUniqueId())));
        return resultHolder.get();
    }

    public boolean setCoins(Player player, int amount) {
        try {
            String statement = "UPDATE `s249_db`.`coins` SET `amount`= ?, `UUID`=?;";
            Object[] variables = {1, UUIDConverter.toByteArray(player.getUniqueId())};
            return database.executeStatement(statement, Arrays.asList(variables));
        } catch (Exception e) {
            Logger.severe("Failed to set " + amount + " for " + player.getName());
            e.printStackTrace();
            return false;
        }
    }

    public void resetCoins(Player player) {
        setCoins(player, 0);
    }

    public boolean addPlayer(Player player) {
        try {
            String insertPlayerSQL = "INSERT INTO `coins` (`UUID`, `amount`) VALUES (?, ?)";
            Object[] variables = {UUIDConverter.toByteArray(player.getUniqueId()), 0};
            return database.executeStatement(insertPlayerSQL, List.of(variables));
        } catch (Exception e) {
            Logger.severe("Failed to add " + player.getName() + " to coins database");
            e.printStackTrace();
            return false;
        }
    }

    public boolean removePlayer(Player player) {
        try {
            String removePlayerSQL = "DELETE FROM s249_db.coins WHERE UUID=?;";
            return database.executeStatement(removePlayerSQL, List.of(UUIDConverter.toByteArray(player.getUniqueId())));
        } catch (Exception e) {
            Logger.severe("Failed to remove " + player.getName() + " from coins database");
            e.printStackTrace();
            return false;
        }
    }
}
