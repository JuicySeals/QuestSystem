package dev.blackgate.questsystem.progression.trackers;

import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class ObtainItemQuestTracker implements Listener {
    private final ProgressionDatabaseManager progressionManager;

    public ObtainItemQuestTracker(ProgressionDatabaseManager progressionDatabaseManager) {
        this.progressionManager = progressionDatabaseManager;
    }

    @EventHandler
    public void onPlayerPickUpItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();

        CompletableFuture<Quest> questFuture = progressionManager.getPlayerQuest(player);
        questFuture.whenCompleteAsync((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to track progress on player quest", throwable);
                return;
            }

            if (quest == null || quest.getQuestType() != QuestType.OBTAIN_ITEM) return;
            quest.getObjectiveItems().stream()
                    .filter(itemStack -> itemStack.getType() == item.getType())
                    .forEach(itemStack -> progressionManager.addProgress(player, item.getAmount()));
            progressionManager.tryToComplete(player);
        });
    }

    @EventHandler
    public void onPlayerDropitem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        CompletableFuture<Quest> questFuture = progressionManager.getPlayerQuest(player);
        questFuture.whenCompleteAsync((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to track progress on player quest", throwable);
                return;
            }

            if (quest == null || quest.getQuestType() != QuestType.OBTAIN_ITEM) return;
            quest.getObjectiveItems().stream()
                    .filter(itemStack -> itemStack.getType() == item.getType())
                    .forEach(itemStack -> progressionManager.removeProgress(player, item.getAmount()));
        });
    }
}
