package dev.blackgate.questsystem.util.inventory.types.numberinput;

import org.bukkit.entity.Player;

public interface NumberInputHandler {
    void onFinish(Player player, int amount);
}
