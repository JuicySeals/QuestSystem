package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuestTypeListener implements Listener {
    private final QuestSystem questSystem;

    public QuestTypeListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null) {
            return;
        }

        String title = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace(" %stage%", ""));
        if (!event.getView().getTitle().startsWith(title)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        QuestCreator questCreator = questSystem.getQuestCreationManager().getQuestCreator(player);

        if (questCreator == null) {
            return;
        }

        if (!event.getView().getTitle().contains("reward")) {
            questCreator.setQuestType(getQuestTypeFromItem(event.getCurrentItem()));
        }

        event.setCancelled(true);
    }
    private QuestType getQuestTypeFromItem(ItemStack item) {
        switch (item.getType()) {
            case NETHERITE_PICKAXE:
                return QuestType.BREAK_BLOCKS;
            case NETHERITE_SWORD:
                return QuestType.KILL_ENTITIES;
            case OAK_LOG:
                return QuestType.PLACE_BLOCKS;
            case NETHERITE_INGOT:
                return QuestType.OBTAIN_ITEM;
            case EXPERIENCE_BOTTLE:
                return QuestType.GET_ACHIEVEMENT;
            default:
                return null;
        }
    }
}
