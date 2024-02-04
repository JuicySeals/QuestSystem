package dev.blackgate.questsystem.util;

import org.bukkit.Bukkit;

public class Logger {

    private static final String PREFIX = "[Quests] ";

    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    public static void severe(String message) {
        Bukkit.getLogger().severe(PREFIX + message);
    }

    public static void info(String message) {
        Bukkit.getLogger().info(PREFIX + message);
    }

    public static void printSQLException(String action, String query, Throwable throwable) {
        Logger.severe(action);
        Logger.severe("Statement: " + query);
        Logger.severe("Message: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public static void printException(String action, Throwable throwable) {
        Logger.severe(action);
        Logger.severe("Message: " + throwable.getMessage());
        throwable.printStackTrace();
    }
}
