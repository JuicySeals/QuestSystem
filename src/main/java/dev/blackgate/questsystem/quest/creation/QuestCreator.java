package dev.blackgate.questsystem.quest.creation;

import de.rapha149.signgui.SignGUI;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.QuestReward;
import dev.blackgate.questsystem.quest.creation.conversations.CommandConversation;
import dev.blackgate.questsystem.quest.creation.gui.QuestRewardTypeGui;
import dev.blackgate.questsystem.quest.creation.gui.QuestTypeGui;
import dev.blackgate.questsystem.quest.creation.gui.QuestXpGui;
import dev.blackgate.questsystem.quest.creation.listeners.QuestRewardTypeListener;
import dev.blackgate.questsystem.quest.creation.listeners.QuestXpGuiListener;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestCreator {
    private Player player;
    private QuestSystem questSystem;
    private String questName, description;
    private QuestType questType;
    private QuestRewardType questRewardType;
    private QuestReward questReward;
    public QuestCreator(Player player, QuestSystem questSystem) {
        this.player = player;
        this.questSystem = questSystem;
        questSystem.getQuestCreationManager().addPlayer(player, this);
        openNamePrompt();
    }

    private void openQuestTypeGui() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace("%stage%", "type");
        QuestTypeGui questTypeGui = new QuestTypeGui(questSystem);
        player.sendMessage(message);
        questTypeGui.open(player);
    }

    // Didn't think it was a good idea to seperate the sign prompts for an entire new class
    private void openNamePrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-details").replace("%stage%", "name");
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
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-details").replace("%stage%", "description");
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
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "name").replace("%value%", name);
        player.sendMessage(message);
    }

    public void setDescription(String description) {
        this.description = description;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "description").replace("%value%", description);
        player.sendMessage(message);
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "quest type").replace("%value%", formatEnumName(questType));
        player.sendMessage(message);
        openRewardTypePrompt();
    }

    private void openRewardTypePrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace("%stage%", "reward type");
        QuestRewardTypeGui questRewardTypeGui = new QuestRewardTypeGui(questSystem);
        questRewardTypeGui.open(player);
        player.sendMessage(message);

    }

    public void setQuestRewardType(QuestRewardType questRewardType) {
        this.questRewardType = questRewardType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "reward type").replace("%value%", formatEnumName(questRewardType));
        player.sendMessage(message);
        openQuestRewardPrompt(questRewardType);
    }

    private void openQuestRewardPrompt(QuestRewardType questRewardType) {
        switch (questRewardType) {
            case COMMAND -> {
                CommandConversation conversation = new CommandConversation(questSystem, player);
                conversation.start();
                questReward = new QuestReward(QuestRewardType.COMMAND, conversation.getCommands());
                create();
            }
            case XP -> {
                QuestXpGui questXpGui = new QuestXpGui(questSystem);
                questXpGui.open(player);
                player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("set-xp"));
            }
        }
    }

    public void setXpAmount(int amount) {
        questReward = new QuestReward(QuestRewardType.XP, amount);
        create();
    }

    private void create() {
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("finished-creating-quest"));
    }

    private String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}