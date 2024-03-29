package dev.blackgate.questsystem.database;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestDatabaseManager {
    public static final String FAILED_QUEST_ID = "Failed to get quest id for quest name: ";
    private final Database database;
    private final QuestSystem questSystem;

    public QuestDatabaseManager(QuestSystem questSystem) {
        this.database = questSystem.getDatabase();
        this.questSystem = questSystem;
        if (database.isConnected()) {
            createTables();
        }
    }

    public void createTables() {
        createQuestsTable();
        createQuestRewardsTable();
        createQuestsRewardItemsTable();
        createQuestsRewardCommandsTable();
        createQuestObjectiveItemsTable();
        createQuestObjectiveTable();
    }

    private void createQuestsTable() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS `quests` (
                 `ID` INT NOT NULL AUTO_INCREMENT,
                 `name` TINYTEXT NOT NULL,
                 `description` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
                 `permission` TINYTEXT NOT NULL COLLATE 'utf8mb4_general_ci',
                 `objective_type` ENUM('BREAK_BLOCKS','KILL_ENTITIES','PLACE_BLOCKS','OBTAIN_ITEM','GET_ACHIEVEMENT') NOT NULL COLLATE 'utf8mb4_general_ci',
                 PRIMARY KEY (`ID`)
                )
                COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSQL), "Failed to create quests table", createTableSQL);
    }

    private void createQuestRewardsTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_rewards` (
                        `ID` INT NOT NULL,
                        `coins` INT NOT NULL DEFAULT 0,
                        `xp` INT NOT NULL DEFAULT 0,
                        PRIMARY KEY (`ID`)
                )
                COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest rewards table", createTableSql);
    }

    private void createQuestsRewardCommandsTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_rewards_commands` (
                    `ID` INT NOT NULL,
                    `command` TEXT NOT NULL COLLATE 'utf8mb4_general_ci'
                ) COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest reward commands table", createTableSql);
    }

    private void createQuestsRewardItemsTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_rewards_items` (
                    `ID` INT NOT NULL,
                    `item` TEXT NOT NULL COLLATE 'utf8mb4_general_ci'
                ) COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest reward item table", createTableSql);
    }

    private void createQuestObjectiveItemsTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_objective_items` (
                	`ID` INT NOT NULL,
                	`item` TEXT NOT NULL
                )
                COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest objective items table", createTableSql);
    }

    private void createQuestObjectiveTable() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_objective` (
                	`ID` INT NULL DEFAULT NULL,
                	`entity` TEXT NULL,
                	`entity_count` INT NULL,
                	`achievement` TEXT NULL
                )
                COLLATE='utf8mb4_general_ci';
                """;
        listenForError(database.executeStatement(createTableSql), "Failed to create quest objective table", createTableSql);
    }

    public void processObjective(Quest quest) {
        QuestType questType = quest.getQuestType();
        int id = quest.getId();
        if (questType == QuestType.OBTAIN_ITEM
                || questType == QuestType.PLACE_BLOCKS
                || questType == QuestType.BREAK_BLOCKS) {
            addObjectiveItems(quest.getObjectiveItems(), id);
        } else {
            addQuestObjective(quest);
        }
    }

    private void addObjectiveItems(List<ItemStack> items, int id) {
        for (ItemStack item : items) {
            String serializedItem = serializeItemStack(item);
            String query = "INSERT INTO `quests_objective_items` (`ID`, `item`) VALUES (?, ?);";
            listenForError(database.executeStatement(query, List.of(id, serializedItem)), "Failed to add objective items to database", query);
        }
    }

    private void addQuestObjective(Quest quest) {
        String query = "";
        LinkedList<Object> variables = new LinkedList<>();
        QuestType questType = quest.getQuestType();
        String name = quest.getObjectiveTaskName();
        variables.add(quest.getId());
        if (questType == QuestType.KILL_ENTITIES) {
            query = "INSERT INTO `quests_objective` (`ID`, `entity`, `entity_count`) VALUES (?, ?, ?);";
            variables.add(name);
            variables.add(quest.getEntityCount());
        } else if (questType == QuestType.GET_ACHIEVEMENT) {
            query = "INSERT INTO `quests_objective` (`ID`, `achievement`) VALUES (?, ?);";
            variables.add(name);
        }
        listenForError(database.executeStatement(query, variables), "Failed to add objective names to database", query);
    }

    public CompletableFuture<Integer> addQuest(Quest quest) {
        String[] variables = {quest.getQuestName(), quest.getDescription(), quest.getPermission(), quest.getQuestType().name()};
        String statement = "INSERT INTO `quests` (`name`, `description`, `permission`, `objective_type`) VALUES (?, ?, ?, ?);";
        return database.executeStatement(statement, List.of(variables))
                .thenComposeAsync(unused -> getQuestId(quest));
    }

    private CompletableFuture<Integer> getQuestId(Quest quest) {
        String[] variables = {quest.getQuestName(), quest.getDescription()};
        String query = "SELECT ID FROM quests WHERE name = ? AND description = ? LIMIT 1;";
        return database.executeQuery(query, List.of(variables))
                .handleAsync((rowSet, exception) -> {
                    if (exception != null) {
                        Logger.printSQLException(FAILED_QUEST_ID + quest.getQuestName(), query, exception);
                        return -1;
                    }
                    try {
                        if (rowSet.next()) {
                            return rowSet.getInt("ID");
                        }
                        return -1;
                    } catch (Exception e) {
                        Logger.printSQLException(FAILED_QUEST_ID + quest.getQuestName(), query, e);
                        return -1;
                    }
                });
    }

    public void processRewards(Quest quest) {
        int[] variables = new int[2];
        int id = quest.getId();
        List<String> commands = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();

        for (QuestReward reward : quest.getRewards()) {
            switch (reward.getRewardType()) {
                case COINS -> variables[0] = reward.getCoinAmount();
                case XP -> variables[1] = reward.getXpAmount();
                case COMMAND -> commands.addAll(reward.getCommands());
                case ITEMS -> items.addAll(reward.getItems());
            }
        }

        addRewardsToDatabase(id, List.of(variables[0], variables[1]));
        addCommandsToDatabase(id, commands);
        addItemsToDatabase(id, items);
    }

    private void addRewardsToDatabase(int id, List<Integer> awards) {
        String statement = """
                INSERT INTO quests_rewards (`ID`, `coins`, `xp`) VALUES (?, ?, ?);
                """;
        database.executeStatement(statement, List.of(id, awards.get(0), awards.get(1)));
    }

    private void addCommandsToDatabase(int id, List<String> commands) {
        String statement = """
                INSERT INTO quests_rewards_commands (`ID`, `command`) VALUES (?, ?);
                """;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String command : commands) {
                    database.executeStatement(statement, List.of(id, command));
                }
            }
        }.runTaskAsynchronously(questSystem); // If there is a lot of commands this mitigates some of the performance decrease of executing a large amount of queries.
    }

    private void addItemsToDatabase(int id, List<ItemStack> items) {
        String statement = """
                INSERT INTO quests_rewards_items (`ID`, `item`) VALUES (?, ?);
                """;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ItemStack item : items) {

                    database.executeStatement(statement, List.of(id, serializeItemStack(item)));
                }
            }
        }.runTaskAsynchronously(questSystem);
    }

    private void listenForError(CompletableFuture<Void> completableFuture, String errorMessage, String query) {
        completableFuture.whenCompleteAsync(((unused, throwable) -> {
            if (throwable != null) {
                Logger.printSQLException(errorMessage, query, throwable);
            }
        }));
    }

    public void resetDatabases() {
        String dropQuestTable = "DROP TABLE quests;";
        String dropQuestRewardsTable = "DROP TABLE quests_rewards";
        String dropQuestsRewardCommandsTable = "DROP TABLE quests_rewards_commands";
        String dropQuestsRewardItemsTable = "DROP TABLE quests_rewards_items;";
        String dropQuestObjectiveTable = "DROP TABLE quests_objective";
        String dropQuestObjectiveItemsTable = "DROP TABLE quests_objective_items";
        listenForError(database.executeStatement(dropQuestTable), "Failed to drop quests table", dropQuestTable);
        listenForError(database.executeStatement(dropQuestRewardsTable), "Failed to drop quest rewards table", dropQuestRewardsTable);
        listenForError(database.executeStatement(dropQuestsRewardCommandsTable), "Failed to drop quest reward commands table", dropQuestsRewardCommandsTable);
        listenForError(database.executeStatement(dropQuestsRewardItemsTable), "Failed to drop quest reward items table", dropQuestsRewardItemsTable);
        listenForError(database.executeStatement(dropQuestObjectiveTable), "Failed to drop quest objective table", dropQuestObjectiveTable);
        listenForError(database.executeStatement(dropQuestObjectiveItemsTable), "Failed to drop quest objective items table", dropQuestObjectiveItemsTable);
        createTables();
    }

    private String serializeItemStack(ItemStack itemStack) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(itemStack);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
