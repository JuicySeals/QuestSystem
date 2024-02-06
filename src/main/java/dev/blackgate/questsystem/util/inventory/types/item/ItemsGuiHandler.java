package dev.blackgate.questsystem.util.inventory.types.item;

import dev.blackgate.questsystem.quest.creation.QuestCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemsGuiHandler {
    void onFinish(List<ItemStack> items, QuestCreator questCreator, Player player);
}
