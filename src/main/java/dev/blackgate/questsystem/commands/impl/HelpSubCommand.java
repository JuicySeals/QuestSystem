package dev.blackgate.questsystem.commands.impl;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpSubCommand implements SubCommand {
    QuestSystem questSystem;

    public HelpSubCommand(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "Quest.Help";
    }

    @Override
    public void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        paramCommandSender.sendMessage(ChatColor.GRAY + "---------------|" + ChatColor.GOLD + " Commands " + ChatColor.GRAY + "|---------------");
        for (SubCommand scmd : this.questSystem.getCommandManager().getSubCommands().values())
            paramCommandSender.sendMessage(ChatColor.YELLOW + scmd.getUsage() + ChatColor.GOLD + " | " + ChatColor.GRAY + scmd.getDesc());
    }

    @Override
    public boolean canConsoleRun() {
        return true;
    }

    @Override
    public String getUsage() {
        return "/" + questSystem.getConfigHelper().getCommand() + " " + getName();
    }

    @Override
    public String getDesc() {
        return "Shows all commands";
    }
}
