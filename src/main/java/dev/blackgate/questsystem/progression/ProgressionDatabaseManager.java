package dev.blackgate.questsystem.progression;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.util.Cache;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProgressionDatabaseManager {
    private final Database database;
    private final ConfigHelper configHelper;
    private final QuestSystem questSystem;
    private final Cache<UUID, Quest> playerQuestCache;
    private final Cache<UUID, Integer> playerQuestProgressCache;

    public ProgressionDatabaseManager(QuestSystem questSystem) {
        this.database = questSystem.getDatabase();
        this.configHelper = questSystem.getConfigHelper();
        this.questSystem = questSystem;
        this.playerQuestCache = new Cache<>(questSystem);
        this.playerQuestProgressCache = new Cache<>(questSystem);
        if (database.isConnected()) {
            createTable();
        }
    }

    private void createTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quest_progression` (
                	`UUID` VARCHAR(36) NOT NULL,
                	`quest_id` INT NOT NULL,
                	`progress` INT NOT NULL,
                	`progress_needed` INT NOT NULL
                )
                COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest progression table", createTableSql);
    }


    public void reset() {
        String dropTable = "DROP TABLE `quest_progression`;";
        listenForError(database.executeStatement(dropTable), "Failed to drop quest progression table", dropTable);
        createTable();
    }

    public void addPlayer(Player player, Quest quest) {
        CompletableFuture<Boolean> playerInQuestFuture = isPlayerInQuest(player);
        playerInQuestFuture.thenAcceptAsync(isInQuest -> {
            if (isInQuest) {
                player.sendMessage(configHelper.getQuestMessage("already-in-quest"));
                return;
            }
            String insertPlayerSql = "INSERT INTO `quest_progression` (`UUID`, `quest_id`, `progress`, `progress_needed`) VALUES (?, ?, ?, ?)";
            String message = configHelper.getQuestMessage("quest-start");
            CompletableFuture<Void> future = database.executeStatement(insertPlayerSql, List.of(player.getUniqueId().toString(), quest.getId(), 0, getProgressNeeded(quest)));
            listenForError(future, "Failed to add player to quest progression tracker", insertPlayerSql);
            player.sendMessage(message.replace("%quest_name%", quest.getQuestName()));
        });
    }


    public CompletableFuture<Boolean> isPlayerInQuest(Player player) {
        if (playerQuestCache.containsKey(player.getUniqueId())) {
            return CompletableFuture.completedFuture(true);
        }

        return getPlayerQuest(player).thenApplyAsync((quest -> {
            if (quest != null) {
                playerQuestCache.addValue(player.getUniqueId(), quest);
                return true;
            } else {
                return false;
            }
        }));
    }

    public CompletableFuture<Quest> getPlayerQuest(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestCache.containsKey(playerUUID)) {
            return CompletableFuture.completedFuture(playerQuestCache.getValue(playerUUID));
        }

        String getPlayerQuestId = "SELECT `quest_id` FROM `quest_progression` WHERE `UUID` = ?";
        return database.executeQuery(getPlayerQuestId, List.of(playerUUID.toString()))
                .thenApplyAsync(resultSet -> {
                    try {
                        if (!resultSet.next()) return null;
                        int questId = resultSet.getInt("quest_id");
                        for (Quest quest : questSystem.getQuestManager().getQuests()) {
                            if (quest.getId() == questId) {
                                playerQuestCache.addValue(player.getUniqueId(), quest);
                                return quest;
                            }
                        }
                    } catch (SQLException exception) {
                        throw new IllegalStateException(exception);
                    }
                    return null;
                })
                .exceptionally(throwable -> {
                    Logger.printSQLException("Failed to get player's quest", getPlayerQuestId, throwable);
                    return null;
                });
    }

    public void removePlayer(Player player) {
        String removePlayerSql = "DELETE FROM `quest_progression` WHERE `UUID` = ?";
        CompletableFuture<Void> future = database.executeStatement(removePlayerSql, List.of(player.getUniqueId().toString()));
        listenForError(future, "Failed to remove player from quest progression tracker", removePlayerSql);
        playerQuestProgressCache.removeFromCache(player.getUniqueId());
        playerQuestCache.removeFromCache(player.getUniqueId());
    }

    public CompletableFuture<Integer> getPlayerQuestProgress(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerQuestProgressCache.containsKey(playerUUID)) {
            return CompletableFuture.completedFuture(playerQuestProgressCache.getValue(playerUUID));
        }

        String getPlayerProgressSql = "SELECT `progress` FROM `quest_progression` WHERE `UUID` = ?";
        return database.executeQuery(getPlayerProgressSql, List.of(playerUUID.toString()))
                .thenApplyAsync(resultSet -> {
                    try {
                        if (resultSet.next()) {
                            int progress = resultSet.getInt("progress");
                            playerQuestProgressCache.addValue(playerUUID, progress);
                            return progress;
                        }
                    } catch (SQLException exception) {
                        throw new IllegalStateException(exception);
                    }
                    return null; // Player progress not found
                })
                .exceptionally(throwable -> {
                    Logger.printSQLException("Failed to get player's quest progress", getPlayerProgressSql, throwable);
                    return null;
                });
    }

    public void addProgress(Player player, int amount) {
        String addProgressSql = "UPDATE `quest_progression` SET `progress` = `progress` + ? WHERE `UUID` = ?";
        CompletableFuture<Void> future = database.executeStatement(addProgressSql, List.of(amount, player.getUniqueId().toString()));
        listenForError(future, "Failed to add progress for player", addProgressSql);
    }

    public void removeProgress(Player player, int amount) {
        getPlayerQuestProgress(player).whenComplete(((integer, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to remove player quest progress", throwable);
                return;
            }
            if (integer == 0) return;
            String addProgressSql = "UPDATE `quest_progression` SET `progress` = `progress` - ? WHERE `UUID` = ?";
            CompletableFuture<Void> future = database.executeStatement(addProgressSql, List.of(amount, player.getUniqueId().toString()));
            listenForError(future, "Failed to remove progress for player", addProgressSql);
        }));
    }

    public int getProgressNeeded(Quest quest) {
        int count = 0;
        switch (quest.getQuestType()) {
            case BREAK_BLOCKS, OBTAIN_ITEM, PLACE_BLOCKS -> {
                for (ItemStack itemStack : quest.getObjectiveItems()) {
                    count += itemStack.getAmount();
                }
            }
            case KILL_ENTITIES -> count = quest.getEntityCount();
            case GET_ACHIEVEMENT -> count = 1;
        }
        return count;
    }

    private void completeQuest(Player player, Quest quest) {
        String messgage = questSystem.getConfigHelper().getQuestMessage("completed-quest")
                .replace("%quest_name%", quest.getQuestName());
        player.sendMessage(messgage);
        removePlayer(player);
        quest.executeRewards(player);
    }

    public void tryToComplete(Player player) {
        CompletableFuture<Integer> progressFuture = getPlayerQuestProgress(player);
        CompletableFuture<Quest> questFuture = getPlayerQuest(player);

        progressFuture.thenCombine(questFuture, (progress, quest) -> {
            if (quest == null) {
                return false;
            }

            int progressNeeded = getProgressNeeded(quest);
            return progress >= progressNeeded;
        }).thenAcceptAsync(isComplete -> {
            if (isComplete) {
                completeQuest(player, questFuture.join()); // On async thread
            }
        }).exceptionally(throwable -> {
            Logger.printException("Failed to check if quest is complete", throwable);
            return null;
        });
    }

    public Cache<UUID, Quest> getPlayerQuestCache() {
        return playerQuestCache;
    }

    public Cache<UUID, Integer> getPlayerQuestProgressCache() {
        return playerQuestProgressCache;
    }

    private void listenForError(CompletableFuture<Void> completableFuture, String errorMessage, String query) {
        completableFuture.whenCompleteAsync(((unused, throwable) -> {
            if (throwable != null) {
                Logger.printSQLException(errorMessage, query, throwable);
            }
        }));
    }
}
