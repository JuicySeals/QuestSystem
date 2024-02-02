package dev.blackgate.questsystem.quest.creation.gui.reward;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.ItemsGui;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestRewardItemGui implements ItemsGui {

    @Override
    public void onFinish(List<ItemStack> items, QuestCreator questCreator) {
        questCreator.setRewardItems(items);
        questCreator.openQuestRewardPrompt(QuestRewardType.COMMAND);
    }
}
