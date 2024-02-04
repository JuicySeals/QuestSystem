package dev.blackgate.questsystem.quest.creation.signs;

import de.rapha149.signgui.SignGUIResult;
import org.bukkit.entity.Player;

public interface SignHandler {
    void onFinish(Player player, SignGUIResult result);
}
