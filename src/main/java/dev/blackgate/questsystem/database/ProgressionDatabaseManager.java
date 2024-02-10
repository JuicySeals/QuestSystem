package dev.blackgate.questsystem.database;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ProgressionDatabaseManager {
    private Database database;
    private ConfigHelper configHelper;
    private QuestSystem questSystem;
    public ProgressionDatabaseManager(QuestSystem questSystem) {
        this.database = questSystem.getDatabase();
        this.configHelper = questSystem.getConfigHelper();
        this.questSystem = questSystem;
        createTable();
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
        return getPlayerQuest(player).thenApplyAsync(Objects::nonNull); // Returns true if not null
    }

    public CompletableFuture<Quest> getPlayerQuest(Player player) {
        String getPlayerQuestId = "SELECT `quest_id` FROM `quest_progression` WHERE `UUID` = ?";
        return database.executeQuery(getPlayerQuestId, List.of(player.getUniqueId().toString()))
                .thenApplyAsync((resultSet) -> {
                    try {
                        if(!resultSet.next()) return null;
                        int questId = resultSet.getInt("quest_id");
                        for (Quest quest : questSystem.getQuestManager().getQuests()) {
                            if (quest.getId() == questId) {
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
    }

    public CompletableFuture<Integer> getPlayerQuestProgress(Player player) {
        String getPlayerProgressSql = "SELECT `progress` FROM `quest_progression` WHERE `UUID` = ?";
        return database.executeQuery(getPlayerProgressSql, List.of(player.getUniqueId().toString()))
                .thenApplyAsync(resultSet -> {
                    try {
                        if(resultSet.next()) {
                            return resultSet.getInt("progress");
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


    public int getProgressNeeded(Quest quest) {
        int count = 0;
        switch (quest.getQuestType()) {
            case BREAK_BLOCKS, OBTAIN_ITEM, PLACE_BLOCKS -> {
                for(ItemStack itemStack : quest.getObjectiveItems()) {
                    count += itemStack.getAmount();
                }
            }
            case KILL_ENTITIES -> count = quest.getEntityCount();
            case GET_ACHIEVEMENT -> count = 1;
        }
        return count;
    }

    private void listenForError(CompletableFuture<Void> completableFuture, String errorMessage, String query) {
        completableFuture.whenCompleteAsync(((unused, throwable) -> {
            if(throwable != null) {
                Logger.printSQLException(errorMessage, query, throwable);
            }
        }));
    }
}
