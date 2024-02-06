package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.QuestDatabaseManager;
import dev.blackgate.questsystem.database.QuestLoader;
import dev.blackgate.questsystem.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestManager {

    private List<Quest> quests;
    private final QuestDatabaseManager databaseManager;
    private QuestSystem questSystem;

    public QuestManager(QuestSystem questSystem) {
        this.questSystem = questSystem;
        databaseManager = new QuestDatabaseManager(questSystem);
        loadQuests();
    }

    private void loadQuests() {
        QuestLoader questLoader = new QuestLoader(questSystem.getDatabase());
        Logger.info("Loading quests...");
        Logger.info("This may take some time.");
        quests = questLoader.getDatabaseQuests().join();
        Logger.info("Finished loading quests");
    }

    public void registerQuest(Quest quest) {
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
        }));

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
