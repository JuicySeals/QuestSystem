package dev.blackgate.questsystem.util.config;

import dev.blackgate.questsystem.QuestSystem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHelper {
  private final FileConfiguration fileConfig;

  public ConfigHelper(QuestSystem questSystem) {
    //TODO ADD COVERAGE TO CHECK IF VALUES EXIST AND VALID
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
    return fileConfig.getString("base-command");
  }

  public String getGeneralMessage(String message) {
    String messageKey = "messages." + message;
    return formatColor(fileConfig.getString(messageKey, ""));
  }

  public String formatColor(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }
}
