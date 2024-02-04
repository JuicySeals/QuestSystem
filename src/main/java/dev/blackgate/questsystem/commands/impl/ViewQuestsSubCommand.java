package dev.blackgate.questsystem.commands.impl;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import dev.blackgate.questsystem.quest.gui.ViewQuestsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewQuestsSubCommand implements SubCommand {
    private final QuestSystem questSystem;

    public ViewQuestsSubCommand(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @Override
    public String getName() {
        return "view";
    }

    @Override
    public String getPermission() {
        return "Quests.View";
    }

    @Override
    public void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        ViewQuestsGui viewQuestsGui = new ViewQuestsGui(questSystem);
        viewQuestsGui.open((Player) paramCommandSender);
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
        return "Views all quests";
    }
}
