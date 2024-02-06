package dev.blackgate.questsystem.quest.creation;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.creation.conversations.CommandConversation;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestCoinGui;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestRewardItemGuiHandler;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestXpGui;
import dev.blackgate.questsystem.quest.creation.gui.type.*;
import dev.blackgate.questsystem.quest.creation.signs.SignPrompt;
import dev.blackgate.questsystem.quest.creation.signs.impl.*;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.inventory.types.item.ItemsGuiHandler;
import dev.blackgate.questsystem.util.inventory.types.numberinput.NumberInputGui;
import dev.blackgate.questsystem.util.inventory.types.item.ItemsGui;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestCreator {
    private final QuestSystem questSystem;
    private final Player player;
    private final List<QuestReward> questRewards;
    private String questName;
    private String description;
    private String permission;
    private QuestType questType;
    private List<ItemStack> questObjectiveItems;
    private String questObjectiveTaskName; // Holds the entity name if kill entities is objective type or achievement name if get achievement objective type is selected
    private int entityCount; // How many entities the player has to kill to finish
    private static final String VALUE_PLACEHOLDER = "%value%";

    public QuestCreator(Player player, QuestSystem questSystem) {
        this.player = player;
        this.questSystem = questSystem;
        this.questRewards = new ArrayList<>();
        questSystem.getQuestCreationManager().addPlayer(player, this);

        openNameSign();
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-name");
        player.sendMessage(message);

    }

    public void openQuestTypeGui() {
        QuestTypeGui questTypeGui = new QuestTypeGui(questSystem);
        questTypeGui.open(player);
    }

    private void openNameSign() {
        QuestNameSign questNameSign = new QuestNameSign(this, questSystem);
        SignPrompt signPrompt = new SignPrompt(questNameSign);
        signPrompt.open(player);
    }

    public void openDescriptionSign() {
        QuestDescriptionSign questDescriptionSign = new QuestDescriptionSign(questSystem, this);
        SignPrompt signPrompt = new SignPrompt(questDescriptionSign);
        signPrompt.open(player);
    }

    public void openPermissionSign() {
        QuestPermissionSign questPermissionSign = new QuestPermissionSign(questSystem, this);
        SignPrompt signPrompt = new SignPrompt(questPermissionSign);
        signPrompt.open(player);
    }

    public void openEntityCountPrompt() {
        NumberInputGui numberInputGui = new NumberInputGui(questSystem, new ItemStack(Material.SKELETON_SKULL), "mob");
        numberInputGui.setHandler(new QuestEntityCount(this));
        numberInputGui.open(player);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setName(String name) {
        this.questName = ChatColor.translateAlternateColorCodes('&', name);
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-name").replace(VALUE_PLACEHOLDER, name);
        player.sendMessage(message);
    }

    public void setDescription(String description) {
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-description").replace(VALUE_PLACEHOLDER, description);
        player.sendMessage(message);
    }

    public void setEntityCount(int count) {
        this.entityCount = count;
        openQuestRewardPrompt(QuestRewardType.XP);
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-type").replace(VALUE_PLACEHOLDER, formatEnumName(questType));
        player.sendMessage(message);
        openDetailsGui(questType);
    }

    public void setEntityType(String entityType) {
        this.questObjectiveTaskName = entityType;
    }

    private void openDetailsGui(QuestType questType) {
        switch (questType) {
            case BREAK_BLOCKS -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("break-blocks-inventory"));
                openObjectiveItemPrompt(new QuestBreakBlocksGuiHandler(questSystem));
            }
            case KILL_ENTITIES -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("input-entity"));
                openKillEntitySign();
            }
            case PLACE_BLOCKS -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-blocks-inventory"));
                openObjectiveItemPrompt(new QuestPlaceBlocksGuiHandler(questSystem));
            }
            case OBTAIN_ITEM -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-items-inventory"));
                openObjectiveItemPrompt(new QuestObtainItemsGuiHandler(questSystem));
            }
            case GET_ACHIEVEMENT -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("input-advancement-name"));
                openAchievementSign();
            }
        }
    }

    private void openObjectiveItemPrompt(ItemsGuiHandler handler) {
        ItemsGui itemsGui = new ItemsGui(questSystem);
        itemsGui.setHandler(handler);
        itemsGui.open(player);
    }

    private void openKillEntitySign() {
        EntityNameSign entityNameSign = new EntityNameSign(questSystem, this);
        SignPrompt signPrompt = new SignPrompt(entityNameSign);
        signPrompt.open(player);

    }

    private void openAchievementSign() {
        AchievementSign achievementSign = new AchievementSign(questSystem, this);
        SignPrompt signPrompt = new SignPrompt(achievementSign);
        signPrompt.open(player);
    }

    public void setAdvancement(String name) {
        this.questObjectiveTaskName = name;
    }

    public void setCommands(List<String> commands) {
        questRewards.add(new QuestReward(QuestRewardType.COMMAND, commands));
        create();
    }

    public void openQuestRewardPrompt(QuestRewardType questRewardType) {
        switch (questRewardType) {
            case COMMAND -> {
                CommandConversation conversation = new CommandConversation(questSystem, player);
                conversation.start();
            }
            case XP -> {
                NumberInputGui numberInputGui = new NumberInputGui(questSystem, new ItemStack(Material.EXPERIENCE_BOTTLE), "level");
                numberInputGui.setHandler(new QuestXpGui(questSystem.getQuestCreationManager()));
                numberInputGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("set-xp"));
            }
            case COINS -> {
                NumberInputGui numberInputGui = new NumberInputGui(questSystem, new ItemStack(Material.GOLD_INGOT), "coin");
                numberInputGui.setHandler(new QuestCoinGui(questSystem.getQuestCreationManager()));
                numberInputGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("set-coins"));
            }
            case ITEMS -> {
                ItemsGui itemsGui = new ItemsGui(questSystem);
                itemsGui.setHandler(new QuestRewardItemGuiHandler());
                itemsGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-items"));
            }
        }
    }

    public void setCoinAmount(int amount) {
        QuestReward questReward = new QuestReward(QuestRewardType.COINS, amount);
        questRewards.add(questReward);
    }

    public void setRewardItems(List<ItemStack> items) {
        questRewards.add(new QuestReward(QuestRewardType.ITEMS, items));
    }

    public void setXpAmount(int amount) {
        QuestReward questReward = new QuestReward(QuestRewardType.XP, amount);
        questRewards.add(questReward);
    }

    private void create() {
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("finished-creating-quest"));
        if(questObjectiveItems != null) {
            questSystem.getQuestManager().registerQuest(new Quest(questName, description, permission, questType, questRewards, questObjectiveItems));
        }else if(questType == QuestType.GET_ACHIEVEMENT){
            questSystem.getQuestManager().registerQuest(new Quest(questName, description, permission, questType, questRewards, questObjectiveTaskName));
        }else if(questType == QuestType.KILL_ENTITIES) {
            questSystem.getQuestManager().registerQuest(new Quest(questName, description, permission, questType, questRewards, questObjectiveTaskName, entityCount));
        }
    }

    public void setQuestObjectiveItems(List<ItemStack> items) {
        this.questObjectiveItems = items;
    }

    private String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}