package dev.blackgate.questsystem.quest;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class QuestReward {
    private static final String WRONG_QUEST_TYPE = "Quest reward type is set to ";
    private final QuestRewardType rewardType;
    private List<?> rewards;
    private int xpAmount;
    private int coinAmount;
    private final QuestSystem questSystem;

    public QuestReward(QuestRewardType type, List<?> rewards, QuestSystem questSystem) {
        if (type != QuestRewardType.ITEMS && type != QuestRewardType.COMMAND) {
            throw new IllegalArgumentException("To supply a list reward type must be items or commands");
        }
        this.rewardType = type;
        this.rewards = rewards;
        this.questSystem = questSystem;
    }

    public QuestReward(QuestRewardType type, int amount, QuestSystem questSystem) {
        this.rewardType = type;
        this.questSystem = questSystem;
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

    public void executeReward(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                switch (rewardType) {
                    case XP -> giveXP(player);
                    case ITEMS -> giveItems(player);
                    case COINS -> questSystem.getCoinManager().addCoins(player, getCoinAmount());
                    case COMMAND -> executeCommands(player);
                }
            }
        }.runTask(questSystem);
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
        player.setLevel(player.getLevel() + xpAmount);
    }

    private void giveItems(Player player) {
        for (Object item : rewards) {
            if (!(item instanceof ItemStack))
                throw new IllegalArgumentException("Reward type set to item but reward isn't an item.");
            player.getInventory().addItem((ItemStack) item);
        }
    }

    private void executeCommands(Player player) {
        for (Object objectCommand : rewards) {
            if (!(objectCommand instanceof String))
                throw new IllegalArgumentException("Reward type set to command but reward isn't a String.");
            String stringCommand = ((String) objectCommand)
                    .replace("%player_name%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringCommand);
        }
    }

    public List<String> getCommands() {
        return (List<String>) rewards;
    }
}
