package dev.blackgate.questsystem.commands.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {
  String getName();
  
  String getPermission();
  
  void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString);
  
  boolean canConsoleRun();
  
  String getUsage();
  
  String getDesc();
}
