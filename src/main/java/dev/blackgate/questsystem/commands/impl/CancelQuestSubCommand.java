package dev.blackgate.questsystem.commands.impl;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelQuestSubCommand implements SubCommand {
    private final QuestSystem questSystem;

    public CancelQuestSubCommand(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getPermission() {
        return "Quests.Cancel";
    }

    @Override
    public void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        Player player = (Player) paramCommandSender;
        questSystem.getProgressionManager().isPlayerInQuest(player).whenComplete(((inQuest, throwable) -> {
            if (inQuest) {
                questSystem.getProgressionManager().removePlayer(player);
                player.sendMessage(questSystem.getConfigHelper().getSubCommandMessage(this, "cancelled"));
            } else {
                player.sendMessage(questSystem.getConfigHelper().getSubCommandMessage(this, "not-in-quest"));
            }
        }));
    }

    @Override
    public boolean canConsoleRun() {
        return false;
    }

    @Override
    public String getUsage() {
        return "/" + questSystem.getConfigHelper().getCommand() + " " + getName();
    }

    @Override
    public String getDesc() {
        return "Cancels your current quest.";
    }
}
