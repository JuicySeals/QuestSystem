package dev.blackgate.questsystem.quest.creation.gui.type;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.types.item.ItemsGuiHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestObtainItemsGuiHandler implements ItemsGuiHandler {
    @Override
    public void onFinish(List<ItemStack> items, QuestCreator questCreator) {
        questCreator.setQuestObjectiveItems(items);
        questCreator.openQuestRewardPrompt(QuestRewardType.XP);
    }
}
