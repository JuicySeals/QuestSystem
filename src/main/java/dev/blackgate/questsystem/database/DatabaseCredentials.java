package dev.blackgate.questsystem.database;

public class DatabaseCredentials {
    private String host;
    private String databaseName;
    private int port;
    private String username;
    private String password;

    public String getHost() {
        return host;
    }

    public DatabaseCredentials setHost(String host) {
        this.host = host;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public DatabaseCredentials setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public int getPort() {
        return port;
    }

    public DatabaseCredentials setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DatabaseCredentials setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DatabaseCredentials setPassword(String password) {
        this.password = password;
        return this;
    }
}
