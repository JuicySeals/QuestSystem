package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.quest.enums.QuestType;

import java.util.List;

public class Quest {
    private final String questName;
    private final String description;
    private final List<QuestReward> rewards;
    private final QuestType questType;

    public Quest(String questName, String description, QuestType questType, List<QuestReward> rewards) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;
        this.questType = questType;
        addToDatabase();
    }

    private void addToDatabase() {
        //TODO
    }

    public String getQuestName() {
        return questName;
    }

    public String getDescription() {
        return description;
    }

    public List<QuestReward> getRewards() {
        return rewards;
    }
}
