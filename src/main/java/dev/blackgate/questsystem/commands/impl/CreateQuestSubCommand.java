package dev.blackgate.questsystem.commands.impl;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateQuestSubCommand implements SubCommand {
    private final QuestSystem questSystem;

    public CreateQuestSubCommand(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "Quest.CreateQuest";
    }

    @Override
    public void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        Player player = (Player) paramCommandSender;
        new QuestCreator(player, questSystem);
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
        return "Creates a quest";
    }
}
