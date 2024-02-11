package dev.blackgate.questsystem.quest.creation.gui.type;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.types.item.ItemsGuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestObtainItemsGuiHandler implements ItemsGuiHandler {
    private final QuestSystem questSystem;

    public QuestObtainItemsGuiHandler(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @Override
    public void onFinish(List<ItemStack> items, QuestCreator questCreator, Player player) {
        if (items.isEmpty()) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
            return;
        }
        questCreator.setQuestObjectiveItems(items);
        questCreator.openQuestRewardPrompt(QuestRewardType.XP);
    }
}
