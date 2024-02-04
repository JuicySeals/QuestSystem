package dev.blackgate.questsystem.quest.creation.signs.impl;

import de.rapha149.signgui.SignGUIResult;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.creation.signs.SignHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestPermissionSign implements SignHandler {
    private final QuestCreator questCreator;
    private final QuestSystem questSystem;

    public QuestPermissionSign(QuestSystem questSystem, QuestCreator questCreator) {
        this.questCreator = questCreator;
        this.questSystem = questSystem;
    }

    @Override
    public void onFinish(Player player, SignGUIResult result) {
        String[] descriptionArray = result.getLines();
        questCreator.setPermission(String.join("", descriptionArray));
        new BukkitRunnable() {
            @Override
            public void run() {
                questCreator.openQuestTypeGui();
                String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type");
                player.sendMessage(message);

            }
        }.runTask(questSystem);
    }
}
