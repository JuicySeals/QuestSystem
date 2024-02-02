package dev.blackgate.questsystem.quest.creation;

import de.rapha149.signgui.SignGUI;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.creation.conversations.CommandConversation;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestCoinGui;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestRewardItemGui;
import dev.blackgate.questsystem.quest.creation.gui.type.QuestBreakBlocksGui;
import dev.blackgate.questsystem.quest.creation.gui.type.QuestObtainItemsGui;
import dev.blackgate.questsystem.quest.creation.gui.type.QuestPlaceBlocksGui;
import dev.blackgate.questsystem.quest.creation.gui.type.QuestTypeGui;
import dev.blackgate.questsystem.quest.creation.gui.reward.QuestXpGui;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.inventory.ItemsGui;
import dev.blackgate.questsystem.util.inventory.QuestItemsGui;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QuestCreator {
    private final Player player;
    public final QuestSystem questSystem;
    private final List<QuestReward> questRewards;
    private String questName;
    private String description;
    private QuestType questType;
    private List<ItemStack> questObjectiveItems;
    private EntityType entityType;
    private Advancement advancement;

    public QuestCreator(Player player, QuestSystem questSystem) {
        this.player = player;
        this.questSystem = questSystem;
        this.questRewards = new ArrayList<>();
        questSystem.getQuestCreationManager().addPlayer(player, this);
        openNamePrompt();
    }

    private void openQuestTypeGui() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type");
        QuestTypeGui questTypeGui = new QuestTypeGui(questSystem);
        player.sendMessage(message);
        questTypeGui.open(player);
    }

    // Didn't think it was a good idea to seperate the sign prompts for an entire new class
    private void openNamePrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-name");
        player.sendMessage(message);
        openNameSign();
    }

    private void openNameSign() {
        SignGUI signGUI = SignGUI.builder().setHandler((p, result) -> {
            String[] nameArray = result.getLines();
            setName(String.join("", nameArray));
            new BukkitRunnable() {
                @Override
                public void run() {
                    openDescriptionPrompt();
                }
            }.runTask(questSystem);
            return Collections.emptyList();
        }).build();
        signGUI.open(player);
    }

    private void openDescriptionPrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-description");
        player.sendMessage(message);
        openDescriptionSign();
    }

    private void openDescriptionSign() {
        SignGUI signGUI = SignGUI.builder()
                .setHandler((p, result) -> {
                    String[] descriptionArray = result.getLines();
                    setDescription(String.join("", descriptionArray));
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            openQuestTypeGui();
                        }
                    }.runTask(questSystem);
                    return Collections.emptyList();
                }).build();
        signGUI.open(player);
    }

    public void setName(String name) {
        this.questName = ChatColor.translateAlternateColorCodes('&', name);
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-name").replace("%value%", name);
        player.sendMessage(message);
    }

    public void setDescription(String description) {
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-description").replace("%value%", description);
        player.sendMessage(message);
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-type").replace("%value%", formatEnumName(questType));
        player.sendMessage(message);
        openDetailsGui(questType);
    }

    private void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    private void openDetailsGui(QuestType questType) {
        switch (questType) {
            case BREAK_BLOCKS -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("break-blocks-inventory"));
                openObjectiveItemPrompt(new QuestBreakBlocksGui());
            }
            case KILL_ENTITIES -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("input-entity"));
                openKillEntitySign();
            }
            case PLACE_BLOCKS -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-blocks-inventory"));
                openObjectiveItemPrompt(new QuestPlaceBlocksGui());
            }
            case OBTAIN_ITEM -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-items-inventory"));
                openObjectiveItemPrompt(new QuestObtainItemsGui());
            }
            case GET_ACHIEVEMENT -> {
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("input-advancement-name"));
                openAchievmentSign();
            }
        }
    }

    private void openObjectiveItemPrompt(ItemsGui handler) {
        QuestItemsGui itemsGui = new QuestItemsGui(questSystem);
        itemsGui.setHandler(handler);
        itemsGui.open(player);
    }

    private void openKillEntitySign() {
        SignGUI signGUI = SignGUI.builder()
                .setHandler((p, result) -> {
                    String[] descriptionArray = result.getLines();
                    String entityName = String.join("", descriptionArray);
                    entityName = entityName.toUpperCase();
                    entityName = entityName.replace(" ", "_");
                    if(isValidEntity(entityName)) {
                        setEntityType(EntityType.valueOf(entityName));
                    }else {
                        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("invalid-entity"));
                        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
                        return Collections.emptyList();
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            openQuestRewardPrompt(QuestRewardType.XP);
                        }
                    }.runTask(questSystem);
                    return Collections.emptyList();
                }).build();
        signGUI.open(player);
    }

    private void openAchievmentSign() {
        SignGUI signGUI = SignGUI.builder()
                .setHandler((p, result) -> {
                    String[] descriptionArray = result.getLines();
                    String advancementName = String.join("", descriptionArray);
                    if(isValidAdvancement(advancementName)) {
                        setAdvancement(getAdvancement(advancementName));
                    }else {
                        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("invalid-advancement"));
                        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
                        return Collections.emptyList();
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            openQuestRewardPrompt(QuestRewardType.XP);
                        }
                    }.runTask(questSystem);
                    return Collections.emptyList();
                }).build();
        signGUI.open(player);
    }

    private void setAdvancement(Advancement advancement) {
        this.advancement = advancement;
    }

    private boolean isValidAdvancement(String name) {
       return getAdvancement(name) != null;
    }

    private Advancement getAdvancement(String name) {
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            if(advancement.getDisplay() == null) continue;
            if(advancement.getDisplay().getTitle() == null) continue;
            if(advancement.getDisplay().getTitle().equalsIgnoreCase(name)) {
                return advancement;
            }
        }
        return null;
    }

    private boolean isValidEntity(String name) {
        try {
            EntityType.valueOf(name);
        }catch (IllegalArgumentException e) {
            return false;
        }
        return true;
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
                QuestXpGui questXpGui = new QuestXpGui(questSystem);
                questXpGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("set-xp"));
            }
            case COINS -> {
                QuestCoinGui questCoinGui = new QuestCoinGui(questSystem);
                questCoinGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("set-coins"));
            }
            case ITEMS -> {
                QuestItemsGui questItemsGui = new QuestItemsGui(questSystem);
                questItemsGui.setHandler(new QuestRewardItemGui());
                questItemsGui.open(player);
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
        questSystem.getQuestManager().registerQuest(new Quest(questName, description, questType, questRewards));
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