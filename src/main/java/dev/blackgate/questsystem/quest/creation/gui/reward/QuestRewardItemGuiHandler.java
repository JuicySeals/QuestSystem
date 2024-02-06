package dev.blackgate.questsystem.quest.creation.gui.reward;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.types.item.ItemsGuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestRewardItemGuiHandler implements ItemsGuiHandler {

    @Override
    public void onFinish(List<ItemStack> items, QuestCreator questCreator, Player player) {
        questCreator.setRewardItems(items);
        questCreator.openQuestRewardPrompt(QuestRewardType.COMMAND);
    }
}
