package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.QuestDatabaseManager;
import dev.blackgate.questsystem.database.QuestLoader;
import dev.blackgate.questsystem.util.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestManager {

    private final QuestDatabaseManager databaseManager;
    private List<Quest> quests;
    private final QuestSystem questSystem;

    public QuestManager(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.databaseManager = new QuestDatabaseManager(questSystem);
        if (questSystem.getDatabase().isConnected()) {
            loadQuests();
        } else {
            quests = Collections.emptyList();
        }
    }

    private void loadQuests() {
        QuestLoader questLoader = new QuestLoader(questSystem.getDatabase(), questSystem);
        Logger.info("Loading quests...");
        Logger.info("This may take some time.");
        quests = questLoader.getDatabaseQuests().join(); // Only on startup so its ok
        Logger.info("Finished loading quests");
    }

    public CompletableFuture<Void> registerQuest(Quest quest) {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        CompletableFuture<Integer> completableFuture = databaseManager.addQuest(quest);
        completableFuture.whenComplete(((id, exception) -> {
            if (exception != null) {
                Logger.printException("Failed to get quest ID", exception);
                return;
            }
            quest.setId(id);
            databaseManager.processRewards(quest);
            databaseManager.processObjective(quest);
            quests.add(quest);
            finished.complete(null);
        }));
        return finished;
    }

    public void resetTables() {
        databaseManager.resetDatabases();
        questSystem.getProgressionManager().reset();
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public Quest getQuest(String name) {
        for (Quest quest : getQuests()) {
            if (quest.getQuestName().equals(name)) {
                return quest;
            }
        }
        return null;
    }

}
