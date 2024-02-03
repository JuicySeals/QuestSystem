package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.sql.rowset.CachedRowSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestManager {
    private List<Quest> quests;
    private Database database;
    private QuestSystem questSystem;
    private final String FAILED_QUEST_ID = "Failed to get quest id for quest name: ";
    public QuestManager(QuestSystem questSystem) {
        this.quests = new ArrayList<>();
        this.database = questSystem.getDatabase();
        this.questSystem = questSystem;
        createQuestsTable();
        createQuestRewardsTable();
        createQuestsRewardCommands();
        createQuestsRewardItems();
    }

    private void createQuestsTable() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS `quests` (
                 `ID` INT NOT NULL AUTO_INCREMENT,
                 `name` TINYTEXT NOT NULL,
                 `description` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
                 `permission` TINYTEXT NOT NULL COLLATE 'utf8mb4_general_ci',
                 `objectivetype` ENUM('BREAK_BLOCKS','KILL_ENTITIES','PLACE_BLOCKS','OBTAIN_ITEM','GET_ACHIEVEMENT') NOT NULL COLLATE 'utf8mb4_general_ci',
                 PRIMARY KEY (`ID`)
                )
                COLLATE='utf8mb4_general_ci';
                """;

        database.executeStatement(createTableSQL, null);
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
        database.executeStatement(createTableSql, null);
    }

    private void createQuestsRewardCommands() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_rewards_commands` (
                    `ID` INT NOT NULL,
                    `command` TEXT NOT NULL COLLATE 'utf8mb4_general_ci'
                ) COLLATE='utf8mb4_general_ci';
                """;
        database.executeStatement(createTableSql, null);
    }

    private void createQuestsRewardItems() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS `quests_rewards_items` (
                    `ID` INT NOT NULL,
                    `item` TEXT NOT NULL COLLATE 'utf8mb4_general_ci'
                ) COLLATE='utf8mb4_general_ci';
                                
                """;
        database.executeStatement(createTableSql, null);
    }

    private void loadQuests() {
        //TODO LOAD FROM DB
    }

    public void registerQuest(Quest quest) {
        addQuestToDatabase(quest);
        CompletableFuture<Integer> completableFuture = getQuestId(quest);
        completableFuture.whenComplete(((integer, exception) -> {
            if (exception != null) {
                Logger.severe(FAILED_QUEST_ID + quest.getQuestName());
                return;
            }
            quest.setId(integer);
        }));
        processRewards(quest);
    }

    private void addQuestToDatabase(Quest quest) {
        String[] variables = {quest.getQuestName(), quest.getDescription(), quest.getPermission(), quest.getQuestType().name()};
        String statement = "INSERT INTO `quests` (`name`, `description`, `permission`, `objectivetype`) VALUES (?, ?, ?, ?);";
        database.executeStatement(statement, List.of(variables));
    }

    private CompletableFuture<Integer> getQuestId(Quest quest) {
        String[] variables = {quest.getQuestName(), quest.getDescription()};
        String query = "SELECT ID FROM quests WHERE name = ? AND description = ? LIMIT 1;";
        CompletableFuture<CachedRowSet> completableFuture = database.executeQuery(query, List.of(variables));
        return completableFuture.handleAsync(((rowSet, exception) -> {
            if (exception != null) {
                Logger.printSQLException(FAILED_QUEST_ID + quest.getQuestName(), query, exception);
                return -1;
            }
            try {
                if (rowSet.next()) {
                    return rowSet.getInt("ID");
                }
            } catch (SQLException e) {
                Logger.printSQLException(FAILED_QUEST_ID + quest.getQuestName(), query, exception);
            }
            return -1;
        }));
    }

    private void processRewards(Quest quest) {
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

    public void unregisterQuest(Quest quest) {
        quests.remove(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }
}
