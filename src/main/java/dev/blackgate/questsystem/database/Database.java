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

    public Database(DatabaseCredentials credentials) {
        this.host = credentials.getHost();
        this.port = credentials.getPort();
        this.username = credentials.getUsername();
        this.databaseName = credentials.getDatabaseName();
        this.password = credentials.getPassword();
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

    public boolean isConnected() {
        return dataSource != null;
    }

    public CompletableFuture<CachedRowSet> executeQuery(String query, List<?> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet resultSet = null;
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                addParameters(preparedStatement, parameters);
                resultSet = preparedStatement.executeQuery();
                CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
                rowSet.populate(resultSet);
                return rowSet;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            } finally {
                if (resultSet != null) try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        });
    }

    public CompletableFuture<CachedRowSet> executeQuery(String query) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet resultSet = null;
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                resultSet = preparedStatement.executeQuery();
                CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
                rowSet.populate(resultSet);
                return rowSet;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            } finally {
                if (resultSet != null) try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        });
    }

    private void addParameters(PreparedStatement statement, List<?> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
    }

    public CompletableFuture<Void> executeStatement(String statement, List<?> variables) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(statement)) {
                addParameters(pstmt, variables);
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

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}