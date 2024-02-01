package dev.blackgate.questsystem.quest.creation;

import de.rapha149.signgui.SignGUI;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.creation.conversations.CommandConversation;
import dev.blackgate.questsystem.quest.creation.gui.QuestCoinGui;
import dev.blackgate.questsystem.quest.creation.gui.QuestItemsGui;
import dev.blackgate.questsystem.quest.creation.gui.QuestTypeGui;
import dev.blackgate.questsystem.quest.creation.gui.QuestXpGui;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.apache.commons.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestCreator {
    private final Player player;
    private final QuestSystem questSystem;
    private String questName;
    private String description;
    private QuestType questType;
    private final List<QuestReward> questRewards;
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
        this.questName = name;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-name").replace("%value%", name);
        player.sendMessage(message);
    }

    public void setDescription(String description) {
        this.description = description;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-description").replace("%value%", description);
        player.sendMessage(message);
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-type").replace("%value%", formatEnumName(questType));
        player.sendMessage(message);
        openQuestRewardPrompt(QuestRewardType.XP);
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
                questItemsGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("place-items"));
            }
        }
    }

    public void setCoinAmount(int amount) {
        questRewards.add(new QuestReward(QuestRewardType.COINS, amount));
    }

    public void setItems(List<ItemStack> items) {
        questRewards.add(new QuestReward(QuestRewardType.ITEMS, items));
    }

    public void setXpAmount(int amount) {
        questRewards.add(new QuestReward(QuestRewardType.XP, amount));
    }

    private void create() {
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("finished-creating-quest"));
        Quest quest = new Quest(questName, description, questType, questRewards);
    }

    private String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}