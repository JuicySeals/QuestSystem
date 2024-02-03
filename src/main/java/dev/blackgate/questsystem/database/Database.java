package dev.blackgate.questsystem.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.blackgate.questsystem.util.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<CachedRowSet> executeQuery(String query, List<?> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = prepareStatementWithParameters(query, parameters);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
                cachedRowSet.populate(resultSet);
                return cachedRowSet;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private PreparedStatement prepareStatementWithParameters(String query, List<?> parameters) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            return preparedStatement;
        }
    }

    public CompletableFuture<Void> executeStatement(String statement, List<?> variables) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement pstmt = prepareStatementWithParameters(statement, variables)) {
                pstmt.executeUpdate();
                return null;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public CompletableFuture<Void> executeStatement(String statement) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(statement)) {
                pstmt.executeUpdate();
                return null;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}