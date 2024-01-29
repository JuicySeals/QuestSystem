package dev.blackgate.questsystem.commands;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.interfaces.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {
  private final Map<String, SubCommand> subCommands = new HashMap<>();
  private final QuestSystem questSystem;

  public CommandManager(QuestSystem questSystem) {
    this.questSystem = questSystem;
  }

  public void registerSubCommand(SubCommand subCommand) {
    this.subCommands.put(subCommand.getName(), subCommand);
  }

  public Map<String, SubCommand> getSubCommands() {
    return this.subCommands;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
      SubCommand help = this.subCommands.get("Help");
      if (!hasPermission(sender, help.getPermission())) {
        sender.sendMessage(questSystem.getConfigHelper().getNoPermission());
        return true;
      }
      help.run(sender, command, label, args);
      return true;
    }

    SubCommand subCommand = this.subCommands.get(args[0]);
    if (subCommand == null) {
      sender.sendMessage(questSystem.getConfigHelper().getGeneralMessage("unknown-sub-command"));
      return true;
    }

    if (!hasPermission(sender, subCommand.getPermission())) {
      sender.sendMessage(questSystem.getConfigHelper().getNoPermission());
      return true;
    }

    if (!subCommand.canConsoleRun() && !(sender instanceof Player)) {
      sender.sendMessage(questSystem.getConfigHelper().getConsoleRan());
      return true;
    }

    subCommand.run(sender, command, label, args);
    return true;
  }

  private boolean hasPermission(CommandSender sender, String permission) {
    return sender.hasPermission(permission) || sender.hasPermission("*");
  }
}
