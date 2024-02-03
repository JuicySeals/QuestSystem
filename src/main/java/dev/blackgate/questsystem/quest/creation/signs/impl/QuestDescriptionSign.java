package dev.blackgate.questsystem.quest.creation.signs.impl;

import de.rapha149.signgui.SignGUIResult;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.creation.signs.SignHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestDescriptionSign implements SignHandler {
    private final QuestSystem questSystem;
    private final QuestCreator questCreator;

    public QuestDescriptionSign(QuestSystem questSystem, QuestCreator questCreator) {
        this.questSystem = questSystem;
        this.questCreator = questCreator;
    }

    @Override
    public void onFinish(Player player, SignGUIResult signGUIResult) {
        String[] descriptionArray = signGUIResult.getLines();
        questCreator.setDescription(String.join("", descriptionArray));
        new BukkitRunnable() {

            @Override
            public void run() {
                questCreator.openPermissionSign();
                String message = questSystem.getConfigHelper().getQuestCreationMessage("input-permission");
                player.sendMessage(message);
            }
        }.runTask(questSystem);

    }
}
