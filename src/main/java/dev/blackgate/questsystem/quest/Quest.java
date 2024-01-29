package dev.blackgate.questsystem.quest;

import java.util.List;

public class Quest {
    private String questName;
    private String description;
    private List<QuestReward> rewards;

    public Quest(String questName, String description, List<QuestReward> rewards) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;

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
