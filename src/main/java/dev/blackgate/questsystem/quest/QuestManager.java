package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.commands.SubCommand;
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
        this.databaseManager = new QuestDatabaseManager(questSystem);
        loadQuests();
    }

    private void loadQuests() {
        QuestLoader questLoader = new QuestLoader(questSystem.getDatabase());
        Logger.info("Loading quests...");
        Logger.info("This may take some time.");
        quests = questLoader.getDatabaseQuests().join(); // Only on startup so its ok
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
    public List<Quest> getQuests() {
        return quests;
    }
    public Quest getQuest(String name) {
        for (Quest quest : getQuests()) {
            System.out.println(quest.getId());
            if (quest.getQuestName().equals(name)) {
                return quest;
            }
        }
        return null;
    }

}
