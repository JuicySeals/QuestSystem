package dev.blackgate.questsystem.quest.creation.gui.reward;

import dev.blackgate.questsystem.quest.creation.QuestCreationManager;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.types.numberinput.NumberInputHandler;
import org.bukkit.entity.Player;

public class QuestXpGui implements NumberInputHandler {
    private final QuestCreationManager questCreationManager;

    public QuestXpGui(QuestCreationManager questCreationManager) {
        this.questCreationManager = questCreationManager;
    }

    @Override
    public void onFinish(Player player, int amount) {
        QuestCreator creator = questCreationManager.getQuestCreator(player);
        creator.setXpAmount(amount);
        creator.openQuestRewardPrompt(QuestRewardType.COINS);
    }
}
