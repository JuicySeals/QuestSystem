package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.quest.enums.QuestType;

import java.util.List;

public class Quest {
    private final String questName;
    private final String description;
    private final List<QuestReward> rewards;
    private final QuestType questType;
    private final String permission;
    private final Database database;
    private int id;

    public Quest(String questName, String description, String permission, QuestType questType, List<QuestReward> rewards, Database database) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;
        this.questType = questType;
        this.permission = permission;
        this.database = database;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public QuestType getQuestType() {
        return questType;
    }

    public String getPermission() {
        return permission;
    }
}
