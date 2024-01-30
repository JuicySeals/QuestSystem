package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class QuestRewardTypeListener implements Listener {
    private QuestSystem questSystem;
    public QuestRewardTypeListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    // Class for each GUI as readiblity is so much easier
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null) {
            return;
        }

        String title = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace(" %stage%", ""));
        if (!event.getView().getTitle().startsWith(title)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        QuestCreator questCreator = questSystem.getQuestCreationManager().getQuestCreator(player);

        if (questCreator == null) {
            return;
        }
        if (event.getView().getTitle().contains("reward")) {
           questCreator.setQuestRewardType(getQuestRewardTypeFromItem(event.getCurrentItem()));
        }
    }

    private QuestRewardType getQuestRewardTypeFromItem(ItemStack item) {
        switch (item.getType()) {
            case COMMAND_BLOCK -> {
                return QuestRewardType.COMMAND;
            }
            case NETHERITE_INGOT -> {
                return QuestRewardType.ITEMS;
            }
            case GOLD_INGOT -> {
                return QuestRewardType.COINS;
            }
            case EXPERIENCE_BOTTLE -> {
                return QuestRewardType.XP;
            }
            default -> {
                return null;
            }
        }
    }
}
