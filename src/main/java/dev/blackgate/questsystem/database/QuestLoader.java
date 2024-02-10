package dev.blackgate.questsystem.database;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.checkerframework.checker.units.qual.C;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.sql.rowset.CachedRowSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class QuestLoader {
    private Database database;
    private List<Quest> quests;
    private CompletableFuture<Void> finishedLoading;
    public QuestLoader(Database database) {
        this.database = database;
        this.quests = new ArrayList<>();
        this.finishedLoading = new CompletableFuture<>();
        load();
    }

    private void load() {
        getQuests().whenComplete((questsLoaded, throwable) -> {
            if (throwable != null) {
                Logger.printException("Failed to get quests", throwable);
                return;
            }
            Iterator<Quest> iterator = questsLoaded.iterator();
            while (iterator.hasNext()) {
                Quest quest = iterator.next();
                int id = quest.getId();

                List<QuestReward> questRewards = new ArrayList<>();
                List<ItemStack> objectiveItems = new ArrayList<>();

                QuestType questType = quest.getQuestType();

                CompletableFuture<List<QuestReward>> getQuestRewards = getQuestRewards(id);
                CompletableFuture<QuestReward> getQuestRewardCommands = getQuestRewardCommands(id);
                CompletableFuture<QuestReward> getQuestRewardItems = getQuestRewardItems(id);
                CompletableFuture<List<ItemStack>> getObjectiveItems = getObjectiveItems(id);
                CompletableFuture<?>[] questFutures = {getQuestRewards, getQuestRewardCommands, getQuestRewardItems, getObjectiveItems};

                getQuestRewards.whenComplete((rewards, rewardsThrowable) -> questRewards.addAll(rewards));
                getQuestRewardCommands.whenComplete((reward, rewardsThrowable) -> questRewards.add(reward));
                getQuestRewardItems.whenComplete((reward, rewardsThrowable) -> questRewards.add(reward));
                getObjectiveItems.whenComplete((items, rewardsThrowable) -> objectiveItems.addAll(items));

                CompletableFuture<Void> rewardFutures = CompletableFuture.allOf(questFutures);
                boolean lastLoop = !iterator.hasNext();
                rewardFutures.whenCompleteAsync((unused, exception) -> {
                    if (exception != null) {
                        Logger.printException("Failed to complete all asynchronous operations loading quests", exception);
                        return;
                    }
                    Quest questToAdd;
                    if (questType == QuestType.KILL_ENTITIES) {
                        CompletableFuture<Integer> entityCount = getEntityCount(id);
                        CompletableFuture<String> entityName = getEntityName(id);
                        questToAdd = new Quest(quest.getQuestName(), quest.getDescription(), quest.getPermission(), quest.getQuestType(), questRewards, entityName.join(), entityCount.join());
                    } else if (questType == QuestType.GET_ACHIEVEMENT) {
                        CompletableFuture<String> achievementName = getAchievementName(id);
                        questToAdd = new Quest(quest.getQuestName(), quest.getDescription(), quest.getPermission(), quest.getQuestType(), questRewards, achievementName.join()); // Blocks the async thread (Not a problem)
                    } else {
                        questToAdd = new Quest(quest.getQuestName(), quest.getDescription(), quest.getPermission(), quest.getQuestType(), questRewards, objectiveItems);
                    }
                    questToAdd.setId(id);
                    quests.add(questToAdd);
                    if(lastLoop) {
                        finishedLoading.complete(null);
                    }
                });
            }
        });

    }

    public CompletableFuture<List<Quest>> getDatabaseQuests() {
        return finishedLoading.thenApply(unused -> quests);
    }

    private CompletableFuture<List<Quest>> getQuests() {
        String query = "SELECT * FROM `quests`;";
        CompletableFuture<CachedRowSet> questsFuture = database.executeQuery(query);
        List<Quest> questsFromDb = new ArrayList<>();
        return questsFuture.thenApply(cachedRowSet -> {
            try {
                while (cachedRowSet.next()) {
                    int id = cachedRowSet.getInt("ID");
                    String name = cachedRowSet.getString("name");
                    String description = cachedRowSet.getString("description");
                    String permission = cachedRowSet.getString("permission");
                    QuestType questType = QuestType.valueOf(cachedRowSet.getString("objective_type"));
                    questsFromDb.add(new Quest(id, name, description, permission, questType));
                }
            } catch (SQLException e) {
                Logger.printSQLException("Error processing result set", query, e.getCause());
                return questsFromDb;
            }
            return questsFromDb;
        }).exceptionally(throwable -> {
            Logger.printSQLException("Failed to load quest details from database", query, throwable);
            return questsFromDb;
        });

    }


    private CompletableFuture<List<QuestReward>> getQuestRewards(int questId) {
        String query = "SELECT * FROM `quests_rewards` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> rewardsFuture = database.executeQuery(query);

        return rewardsFuture.thenApplyAsync(cachedRowSet -> {
            List<QuestReward> rewards = new ArrayList<>();

            try {
                while (cachedRowSet.next()) {
                    int xp = cachedRowSet.getInt("xp");
                    int coins = cachedRowSet.getInt("coins");
                    rewards.add(new QuestReward(QuestRewardType.XP, xp));
                    rewards.add(new QuestReward(QuestRewardType.COINS, coins));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return rewards;
        });
    }

    private CompletableFuture<QuestReward> getQuestRewardCommands(int questId) {
        String query = "SELECT * FROM `quests_rewards_commands` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> rewardsFuture = database.executeQuery(query);

        return rewardsFuture.thenApplyAsync(cachedRowSet -> {
            List<String> commands = new ArrayList<>();
            try {
                while (cachedRowSet.next()) {
                    commands.add(cachedRowSet.getString("command"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new QuestReward(QuestRewardType.COMMAND, commands);
        });
    }

    private CompletableFuture<QuestReward> getQuestRewardItems(int questId) {
        String query = "SELECT `item` FROM `quests_rewards_items` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> itemsFuture = database.executeQuery(query);

        return itemsFuture.thenApplyAsync(cachedRowSet -> {
            List<ItemStack> items = extractItems(cachedRowSet);
            return new QuestReward(QuestRewardType.ITEMS, items);
        });
    }

    private List<ItemStack> extractItems(CachedRowSet cachedRowSet) {
        List<ItemStack> items = new ArrayList<>();
        try {
            while (cachedRowSet.next()) {
                String serializedItem = cachedRowSet.getString("item");
                CompletableFuture<ItemStack> itemStack = deserializeItemStack(serializedItem);
                items.add(itemStack.join()); // Join doesnt block main thread as the code is run on the ForkJoinPool thread blocking that until its deserialized
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    private CompletableFuture<List<ItemStack>> getObjectiveItems(int questId) {
        String query = "SELECT `item` FROM `quests_objective_items` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> itemsFuture = database.executeQuery(query);
        return itemsFuture.thenApplyAsync(this::extractItems);
    }


    private CompletableFuture<Integer> getEntityCount(int questId) {
        String query = "SELECT `entity_count` FROM `quests_objective` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> countFuture = database.executeQuery(query);

        return countFuture.thenApplyAsync(cachedRowSet -> {
            try {
                if (cachedRowSet.next()) {
                    return cachedRowSet.getInt("entity_count");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return 0; // Default value if no data is found
        });
    }

    private CompletableFuture<String> getEntityName(int questId) {
        String query = "SELECT `entity` FROM `quests_objective` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> countFuture = database.executeQuery(query);

        return countFuture.thenApplyAsync(cachedRowSet -> {
            try {
                if (cachedRowSet.next()) {
                    return cachedRowSet.getString("entity");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null; // Default value if no data is found
        });
    }

    private CompletableFuture<String> getAchievementName(int questId) {
        String query = "SELECT `achievement` FROM `quests_objective` WHERE `ID` = " + questId + ";";
        CompletableFuture<CachedRowSet> achievementFuture = database.executeQuery(query);

        return achievementFuture.thenApplyAsync(cachedRowSet -> {
            try {
                if (cachedRowSet.next()) {
                    return cachedRowSet.getString("achievement");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null; // Default value if no data is found
        });
    }

    // All IO can't be on the main thread :( (Applicant PDF said so)
    private CompletableFuture<ItemStack> deserializeItemStack(String serializedItem) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] data = Base64Coder.decodeLines(serializedItem);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                return (ItemStack) dataInput.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            return null;
        });
    }
}
