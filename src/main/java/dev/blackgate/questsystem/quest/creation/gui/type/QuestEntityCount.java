package dev.blackgate.questsystem.quest.creation.gui.type;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.util.inventory.types.numberinput.NumberInputHandler;
import org.bukkit.entity.Player;

public class QuestEntityCount implements NumberInputHandler {
    private final QuestCreator questCreator;

    public QuestEntityCount(QuestCreator questCreator) {
        this.questCreator = questCreator;
    }

    @Override
    public void onFinish(Player player, int amount) {
        questCreator.setEntityCount(amount);
    }
}
