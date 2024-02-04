package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.QuestDatabaseManager;
import dev.blackgate.questsystem.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestManager {

    private final List<Quest> quests;
    private final QuestDatabaseManager databaseManager;

    public QuestManager(QuestSystem questSystem) {
        this.quests = new ArrayList<>();
        databaseManager = new QuestDatabaseManager(questSystem);
    }

    private void loadQuests() {
        //TODO LOAD FROM DB
    }

    public void registerQuest(Quest quest) {
        CompletableFuture<Integer> completableFuture = databaseManager.addQuestToDatabase(quest);
        completableFuture.whenComplete(((integer, exception) -> {
            if (exception != null) {
                Logger.severe(QuestDatabaseManager.FAILED_QUEST_ID + quest.getQuestName());
                return;
            }
            quest.setId(integer);
        }));
        databaseManager.processRewards(quest);
        databaseManager.processObjective(quest);
    }

    public void resetDatabases() {
        databaseManager.resetDatabases();
    }
    public void unregisterQuest(Quest quest) {
        quests.remove(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }
}
