package dev.blackgate.questsystem.util.inventory.types.confirm;

import org.bukkit.entity.Player;


public interface ConfirmGuiHandler {
    void onFinish(boolean isConfirmed, Player player);
}
