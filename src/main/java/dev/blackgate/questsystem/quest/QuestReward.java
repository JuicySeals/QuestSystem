package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class QuestReward {
    private final QuestRewardType rewardType;
    private final static String WRONG_QUEST_TYPE = "Quest reward type is set to ";
    private List<?> rewards;
    private int xpAmount;
    private int coinAmount;

    public QuestReward(QuestRewardType type, List<?> rewards) {
        if (type != QuestRewardType.ITEMS && type != QuestRewardType.COMMAND) {
            throw new IllegalArgumentException("To supply a list reward type must be items or commands");
        }
        this.rewardType = type;
        this.rewards = rewards;
    }

    public QuestReward(QuestRewardType type, int amount) {
        this.rewardType = type;
        if (type == QuestRewardType.XP) {
            this.xpAmount = amount;
        } else if (rewardType == QuestRewardType.COINS) {
            this.coinAmount = amount;
        } else {
            throw new IllegalArgumentException("To supply an integer reward type must be XP or coins.");
        }
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

    public List<ItemStack> getItems() {
        if (getRewardType() != QuestRewardType.ITEMS)
            throw new UnsupportedOperationException(WRONG_QUEST_TYPE + getRewardType());
        return (List<ItemStack>) rewards;
    }

    public int getXpAmount() {
        if (getRewardType() != QuestRewardType.XP)
            throw new UnsupportedOperationException(WRONG_QUEST_TYPE + getRewardType());
        return xpAmount;
    }

    public int getCoinAmount() {
        if (getRewardType() != QuestRewardType.COINS)
            throw new UnsupportedOperationException(WRONG_QUEST_TYPE + getRewardType());
        return coinAmount;
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

    public List<String> getCommands() {
        return (List<String>) rewards;
    }
}
