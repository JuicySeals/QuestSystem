package dev.blackgate.questsystem.progression.trackers;

import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.concurrent.CompletableFuture;

public class AchievementTracker implements Listener {
    private final ProgressionDatabaseManager progressionManager;

    public AchievementTracker(ProgressionDatabaseManager progressionDatabaseManager) {
        this.progressionManager = progressionDatabaseManager;
    }

    @EventHandler
    public void playerGetAchievement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        if (advancement.getDisplay() == null) return;

        CompletableFuture<Quest> questFuture = progressionManager.getPlayerQuest(player);
        questFuture.whenCompleteAsync((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to track progress on player quest", throwable);
                return;
            }

            if (quest == null || quest.getQuestType() != QuestType.GET_ACHIEVEMENT) return;
            if (!quest.getObjectiveTaskName().equalsIgnoreCase(advancement.getDisplay().getTitle())) return;
            progressionManager.addProgress(player, 1);
            progressionManager.tryToComplete(player);
        });
    }
}
