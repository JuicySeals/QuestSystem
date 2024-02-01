package dev.blackgate.questsystem.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import static dev.blackgate.questsystem.util.UUIDConverter.toByteArray;

public class Database {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String databaseName;
    private HikariDataSource dataSource;

    public Database(String host, int port, String username, String password, String databaseName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.databaseName = databaseName;
        this.password = password;
        connect();
        createCoinTable();
        createQuestsTable();
    }

    private void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName));
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception exception) {
            Logger.severe("Failed to connect to the database: " + exception.getMessage());
            // Hikari prints exception already
        }
    }

    private void createCoinTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `coins` ("
                + "`UUID` BINARY(16) NOT NULL,"
                + "`amount` BIGINT NOT NULL DEFAULT 0,"
                + "PRIMARY KEY (`UUID`)"
                + ");";

        executeStatement(createTableSQL);
    }

    private void createQuestsTable() {
        String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS `quests` (
                    `name` VARCHAR(50) NOT NULL,
                    `description` VARCHAR(20) NOT NULL,
                    `rewardtype` TINYINT NOT NULL,
                    `rewards` VARCHAR(20) NOT NULL
                    );
                """;

        executeStatement(createTableSQL);
    }

    public void executeQuery(String query, Consumer<ResultSet> resultSetConsumer, List<?> parameters) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = prepareStatementWithParameters(query, parameters);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSetConsumer.accept(resultSet);
        } catch (SQLException e) {
            Logger.severe("Failed to execute query: " + query + ". Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareStatementWithParameters(String query, List<?> parameters) throws SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }
        return preparedStatement;
    }

    public boolean executeStatement(String statement, List<?> variables) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = prepareStatementWithParameters(statement, variables)) {
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Logger.severe("Failed to execute statement: " + statement + ". Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeStatement(String statement) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Logger.severe("Failed to execute statement: " + statement + ". Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlayerInDatabase(Player player) {
        boolean playerExists = false;
        String checkPlayerSQL = "SELECT 1 FROM `coins` WHERE `UUID` = ? LIMIT 1;";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(checkPlayerSQL)) {
            preparedStatement.setBytes(1, toByteArray(player.getUniqueId()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                playerExists = resultSet.next();
            }

        } catch (SQLException e) {
            Logger.severe("Failed to check if " + player.getName() + " is in the database. Error: " + e.getMessage());
            e.printStackTrace();
        }

        return playerExists;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}