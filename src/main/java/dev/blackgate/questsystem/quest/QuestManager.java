package dev.blackgate.questsystem.quest;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private List<Quest> quests;
    public QuestManager() {
        this.quests = new ArrayList<>();
    }

    public void registerQuest(Quest quest) {
        quests.add(quest);
    }

    public void unregisterQuest(Quest quest) {
        quests.remove(quest);
    }
}
