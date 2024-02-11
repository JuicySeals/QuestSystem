package dev.blackgate.questsystem.progression.trackers;

import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.CompletableFuture;

public class BreakBlocksTracker implements Listener {
    private final ProgressionDatabaseManager progressionManager;

    public BreakBlocksTracker(ProgressionDatabaseManager progressionDatabaseManager) {
        this.progressionManager = progressionDatabaseManager;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        CompletableFuture<Quest> questFuture = progressionManager.getPlayerQuest(player);
        questFuture.whenCompleteAsync((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to track progress on player quest", throwable);
                return;
            }

            if (quest == null || quest.getQuestType() != QuestType.BREAK_BLOCKS) return;
            quest.getObjectiveItems().stream()
                    .filter(itemStack -> itemStack.getType() == blockType)
                    .forEach(itemStack -> progressionManager.addProgress(player, 1));
            progressionManager.tryToComplete(player);
        });
    }
}
