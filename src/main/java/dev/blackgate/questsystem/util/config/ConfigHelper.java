package dev.blackgate.questsystem.util.config;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHelper {
    private final FileConfiguration fileConfig;

    public ConfigHelper(QuestSystem questSystem) {
        this.fileConfig = questSystem.getConfig();
    }

    public String getNoPermission() {
        return getGeneralMessage("no-permission");
    }

    public String getConsoleRan() {
        return getGeneralMessage("must-be-player");
    }

    public String getIncorrectUsage(String usage) {
        return getGeneralMessage("incorrect-usage").replace("%usage%", usage);
    }

    public String getCommand() {
        return "quests";
    }

    public String getGeneralMessage(String message) {
        String messageKey = "messages." + message;
        return formatColor(fileConfig.getString(messageKey, ""));
    }

    public String getQuestCreationMessage(String message) {
        String messageKey = "quest-creation-messages." + message;
        return formatColor(fileConfig.getString(messageKey, ""));
    }

    public String getQuestMessage(String message) {
        String messageKey = "quest." + message;
        return formatColor(fileConfig.getString(messageKey, ""));
    }

    public String getSubCommandMessage(SubCommand subCommand, String message) {
        return formatColor(fileConfig.getString("command-messages." + subCommand.getName() + "." + message, ""));
    }

    public static String formatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String getString(String path) {
        return formatColor(fileConfig.getString(path));
    }
}
