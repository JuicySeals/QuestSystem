package dev.blackgate.questsystem.util;

import org.bukkit.Bukkit;

public class Logger {

    private static final String PREFIX = "[Quests] ";
    private static Logger logger;
    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    public static void setLogger(Logger logger) {
        Logger.logger = logger;
    }

    public static void severe(String message) {
        Bukkit.getLogger().severe(PREFIX + message);
    }

    public static void info(String message) {
        Bukkit.getLogger().info(PREFIX + message);
    }
}
