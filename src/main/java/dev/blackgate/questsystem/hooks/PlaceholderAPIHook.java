package dev.blackgate.questsystem.hooks;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.progression.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.util.Cache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final Cache<UUID, Quest> playerQuestCache;
    private final Cache<UUID, Integer> playerQuestProgressCache;
    private final ProgressionDatabaseManager progessionManager;

    public PlaceholderAPIHook(QuestSystem questSystem) {
        this.progessionManager = questSystem.getProgressionManager();
        this.playerQuestCache = progessionManager.getPlayerQuestCache();
        this.playerQuestProgressCache = progessionManager.getPlayerQuestProgressCache();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "quest";
    }

    @Override
    public @NotNull String getAuthor() {
        return "JuicySeals";
    }

    @Override
    public @NotNull String getVersion() {
        return "1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        switch (params) {
            case "name" -> {
                return getName(player);
            }
            case "progress" -> {
                return String.valueOf(getProgress(player));
            }
            case "progress_needed" -> {
                return String.valueOf(getProgressNeeeded(player));
            }
            case "description" -> {
                return getDescription(player);
            }
            default -> {
                return null;
            }
        }
    }

    private String getName(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestCache.containsKey(playerUUID)) return playerQuestCache.getValue(playerUUID).getQuestName();
        return progessionManager.getPlayerQuest(player).join().getQuestName(); // Only way unfortunately there is a timeout though
    }

    private String getDescription(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestCache.containsKey(playerUUID)) return playerQuestCache.getValue(playerUUID).getDescription();
        return progessionManager.getPlayerQuest(player).join().getDescription();
    }

    private int getProgress(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestProgressCache.containsKey(playerUUID)) return playerQuestProgressCache.getValue(playerUUID);
        return progessionManager.getPlayerQuestProgress(player).join();

    }

    private int getProgressNeeeded(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestCache.containsKey(playerUUID)) {
            return progessionManager.getProgressNeeded(playerQuestCache.getValue(playerUUID));
        }
        return progessionManager.getProgressNeeded(progessionManager.getPlayerQuest(player).join());
    }
}
