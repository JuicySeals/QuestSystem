package dev.blackgate.questsystem.commands.impl;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
import dev.blackgate.questsystem.util.inventory.types.confirm.ConfirmGui;
import dev.blackgate.questsystem.util.inventory.types.confirm.ConfirmGuiHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetQuestsSubCommand implements SubCommand, ConfirmGuiHandler {
    private final QuestSystem questSystem;
    public ResetQuestsSubCommand(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getPermission() {
        return "Quests.Reset";
    }

    @Override
    public void run(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        ConfirmGui confirmGui = new ConfirmGui(questSystem);
        Player player = (Player) paramCommandSender;
        confirmGui.setHandler(this);
        confirmGui.open(player);
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
        return "Deletes all quests";
    }

    @Override
    public void onFinish(boolean isConfirmed, Player player) {
        if(isConfirmed) {
            String message = questSystem.getConfigHelper().getSubCommandMessage(this, "reset-message");
            questSystem.getQuestManager().resetDatabases();
            player.sendMessage(message);
        }
    }
}
