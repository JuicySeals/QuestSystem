package dev.blackgate.questsystem.util;

import org.bukkit.Bukkit;

public class Logger {

    private Logger() {
        throw new IllegalStateException("Utility class");
    }
    private static final String PREFIX = "[Quests] ";
    public static void severe(String message) {
        Bukkit.getLogger().severe(PREFIX + message);
    }

    public static void info(String message) {
        Bukkit.getLogger().info(PREFIX + message);
    }
}
