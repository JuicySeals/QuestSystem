package dev.blackgate.questsystem.quest.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinQuestInfoListener implements Listener {
    private final FileConfiguration fileConfiguration;
    private final ProgressionDatabaseManager progressionDatabaseManager;

    public PlayerJoinQuestInfoListener(QuestSystem questSystem) {
        this.fileConfiguration = questSystem.getConfig();
        this.progressionDatabaseManager = questSystem.getProgressionManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        progressionDatabaseManager.getPlayerQuest(player).whenCompleteAsync(((quest, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to send welcome message", throwable);
                return;
            }
            if (quest == null) return;
            List<String> messages = fileConfiguration.getStringList("player-join-message");
            for (String message : messages) {
                message = message
                        .replace("%quest_name%", quest.getQuestName())
                        .replace("%quest_progress%", String.valueOf(progressionDatabaseManager.getPlayerQuestProgress(player).join()))
                        .replace("%quest_progress_needed%", String.valueOf(progressionDatabaseManager.getProgressNeeded(quest)))
                        .replace("%player_name%", player.getName());
                message = ConfigHelper.formatColor(message);
                player.sendMessage(message);
            }
        }));
    }
}
