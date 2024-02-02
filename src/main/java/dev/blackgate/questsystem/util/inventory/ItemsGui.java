package dev.blackgate.questsystem.util.inventory;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemsGui {
    public void onFinish(List<ItemStack> items, QuestCreator questCreator);
}
