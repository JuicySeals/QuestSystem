package dev.blackgate.questsystem.progression.trackers;

import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.CompletableFuture;

public class KillEntitiesTracker implements Listener {
    private final ProgressionDatabaseManager progressionManager;

    public KillEntitiesTracker(ProgressionDatabaseManager progressionDatabaseManager) {
        this.progressionManager = progressionDatabaseManager;
    }

    @EventHandler
    public void killEntityEvent(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        EntityType entityType = event.getEntityType();
        Player killer = event.getEntity().getKiller();

        CompletableFuture<Quest> questFuture = progressionManager.getPlayerQuest(killer);
        questFuture.whenCompleteAsync((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to track progress on player quest", throwable);
                return;
            }

            if (quest == null || quest.getQuestType() != QuestType.KILL_ENTITIES) return;
            if (!entityType.toString().equalsIgnoreCase(quest.getObjectiveTaskName())) return;
            progressionManager.addProgress(killer, 1);
            progressionManager.tryToComplete(killer);
        });
    }
}
