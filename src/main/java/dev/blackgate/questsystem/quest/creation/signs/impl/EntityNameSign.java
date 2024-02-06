package dev.blackgate.questsystem.quest.creation.signs.impl;

import de.rapha149.signgui.SignGUIResult;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.creation.signs.SignHandler;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityNameSign implements SignHandler {
    private final QuestSystem questSystem;
    private final QuestCreator questCreator;

    public EntityNameSign(QuestSystem questSystem, QuestCreator questCreator) {
        this.questSystem = questSystem;
        this.questCreator = questCreator;
    }

    @Override
    public void onFinish(Player player, SignGUIResult signGUIResult) {
        String[] descriptionArray = signGUIResult.getLines();
        String entityName = String.join("", descriptionArray);
        entityName = entityName.toUpperCase();
        entityName = entityName.replace(" ", "_");
        if (isValidEntity(entityName)) {
            questCreator.setEntityType(entityName);
        } else {
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("invalid-entity"));
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            return;
        }
        new BukkitRunnable() { // This fixed a stack overflow error by delaying it a tick
            @Override
            public void run() {
                questCreator.openEntityCountPrompt();
            }
        }.runTask(questSystem);
    }

    private boolean isValidEntity(String name) {
        try {
            EntityType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
