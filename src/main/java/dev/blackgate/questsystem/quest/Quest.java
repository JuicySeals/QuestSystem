package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private final String questName;
    private final String description;
    private final List<QuestReward> rewards;
    private final QuestType questType;
    private final String permission;
    private List<ItemStack> objectiveItems;
    private int entityCount; // How many entities the player has to kill to finish
    private String objectiveName;
    private int id;
    // Place blocks,break blocks,obtain items objective type constructor
    public Quest(String questName, String description, String permission, QuestType questType, List<QuestReward> rewards, List<ItemStack> objectiveItems) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;
        this.questType = questType;
        this.permission = permission;
        this.objectiveItems = objectiveItems;
    }

    // Get achievement
    public Quest(String questName, String description, String permission, QuestType questType, List<QuestReward> rewards, String achievementName) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;
        this.questType = questType;
        this.permission = permission;
        this.objectiveName = achievementName;
    }

    // Kill entity objevtive type
    public Quest(String questName, String description, String permission, QuestType questType, List<QuestReward> rewards, String entityName, int entityCount) {
        this.questName = questName;
        this.description = description;
        this.rewards = rewards;
        this.questType = questType;
        this.permission = permission;
        this.objectiveName = entityName;
        this.entityCount = entityCount;
    }

    // For loading quests through DB before gathering of other data
    public Quest(int id, String questName, String description, String permission, QuestType questType) {
        this.id = id;
        this.questName = questName;
        this.description = description;
        this.questType = questType;
        this.permission = permission;
        this.rewards = new ArrayList<>();
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
    public List<ItemStack> getObjectiveItems() {
        return objectiveItems;
    }

    public int getEntityCount() {
        return entityCount;
    }
    public String getObjectiveTaskName() { // Struggled to think of a describing title for this method. Returns the entity name or achievement name
        return objectiveName;
    }
}
