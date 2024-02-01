package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestReward {
    private final QuestRewardType rewardType;
    private List<?> rewards;
    private int xpAmount;

    public QuestReward(QuestRewardType type, List<?> rewards) {
        this.rewardType = type;
        this.rewards = rewards;
    }

    public QuestReward(QuestRewardType type, int xpAmount) {
        this.rewardType = type;
        this.xpAmount = xpAmount;
    }

    public QuestRewardType getRewardType() {
        return rewardType;
    }

    public void executeRewards(Player player) {
        switch (rewardType) {
            case XP -> giveXP(player);
            case ITEMS -> giveItems(player);
            case COINS -> {
                //TODO
            }
            case COMMAND -> executeCommands(player);
        }
    }

    private void giveXP(Player player) {
        player.setExp(player.getExp() + xpAmount);
    }

    private void giveItems(Player player) {
        for (Object item : rewards) {
            if (!(item instanceof ItemStack))
                throw new IllegalArgumentException("Reward type set to item but reward isn't an item.");
            player.getInventory().addItem((ItemStack) item);
        }
    }

    private void executeCommands(Player player) {
        for (Object command : rewards) {
            if (!(command instanceof String))
                throw new IllegalArgumentException("Reward type set to command but reward isn't a String.");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ((String) command).replace("%player%", player.getName()));
        }
    }
}
