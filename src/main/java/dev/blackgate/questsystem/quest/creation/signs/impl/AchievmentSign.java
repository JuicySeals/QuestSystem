package dev.blackgate.questsystem.quest.creation.signs.impl;

import de.rapha149.signgui.SignGUIResult;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.creation.signs.SignHandler;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class AchievmentSign implements SignHandler {
    private final QuestCreator questCreator;
    private final QuestSystem questSystem;

    public AchievmentSign(QuestSystem questSystem, QuestCreator questCreator) {
        this.questCreator = questCreator;
        this.questSystem = questSystem;
    }

    @Override
    public void onFinish(Player player, SignGUIResult signGUIResult) {
        String[] descriptionArray = signGUIResult.getLines();
        String advancementName = String.join("", descriptionArray);
        if (isValidAdvancement(advancementName)) {
            questCreator.setAdvancement(getAdvancement(advancementName));
        } else {
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("invalid-advancement"));
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                questCreator.openQuestRewardPrompt(QuestRewardType.XP);
            }
        }.runTask(questSystem);
    }

    private boolean isValidAdvancement(String name) {
        return getAdvancement(name) != null;
    }

    private Advancement getAdvancement(String name) {
        Iterator<Advancement> iterator = Bukkit.advancementIterator();
        while (iterator.hasNext()) {
            Advancement advancement = iterator.next();
            if (advancement.getDisplay() == null) continue;
            if (advancement.getDisplay().getTitle().equalsIgnoreCase(name)) {
                return advancement;
            }
        }
        return null;
    }
}
