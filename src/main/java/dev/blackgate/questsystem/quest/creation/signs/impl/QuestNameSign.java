package dev.blackgate.questsystem.quest.creation.signs.impl;

import de.rapha149.signgui.SignGUIResult;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.creation.signs.SignHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestNameSign implements SignHandler {
    private QuestCreator questCreator;
    private QuestSystem questSystem;

    public QuestNameSign(QuestCreator questCreator, QuestSystem questSystem) {
        this.questCreator = questCreator;
        this.questSystem = questSystem;
    }

    @Override
    public void onFinish(Player player, SignGUIResult result) {
        String[] nameArray = result.getLines();
        questCreator.setName(String.join("", nameArray));
        new BukkitRunnable() {
            @Override
            public void run() {
                questCreator.openDescriptionSign();
                String message = questSystem.getConfigHelper().getQuestCreationMessage("input-description");
                player.sendMessage(message);
            }
        }.runTask(questSystem);
    }
}
