package dev.blackgate.questsystem.quest.creation;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class QuestCreationManager {
    private HashMap<Player, QuestCreator> activeSessions = new HashMap<>();

    public void addPlayer(Player player, QuestCreator questCreator) {
        activeSessions.put(player, questCreator);
    }

    public void removePlayer(Player player) {
        activeSessions.remove(player);
    }

    public QuestCreator getQuestCreator(Player player) {
        return activeSessions.get(player);
    }

}
