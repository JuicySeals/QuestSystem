package dev.blackgate.questsystem.quest.creation;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class QuestCreationManager {
    private HashMap<UUID, QuestCreator> activeSessions = new HashMap<>();

    public void addPlayer(Player player, QuestCreator questCreator) {
        activeSessions.put(player.getUniqueId(), questCreator);
    }

    public void removePlayer(Player player) {
        activeSessions.remove(player.getUniqueId());
    }

    public QuestCreator getQuestCreator(Player player) {
        return activeSessions.get(player.getUniqueId());
    }

}
