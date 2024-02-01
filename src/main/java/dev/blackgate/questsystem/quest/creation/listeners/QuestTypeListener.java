package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class QuestTypeListener implements Listener {
    private final QuestSystem questSystem;
    private final String typeTitle;
    private boolean isSet;

    public QuestTypeListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.typeTitle = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace(" %stage%", ""));
        this.isSet = false;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null) {
            return;
        }

        if (!event.getView().getTitle().startsWith(typeTitle)) {
            return;
        }

        QuestCreator questCreator = questSystem.getQuestCreationManager().getQuestCreator(player);

        if (questCreator == null) {
            return;
        }
        isSet = true;
        questCreator.setQuestType(getQuestTypeFromItem(event.getCurrentItem()));
        event.setCancelled(true);
    }
    private QuestType getQuestTypeFromItem(ItemStack item) {
        return switch (item.getType()) {
            case NETHERITE_PICKAXE -> QuestType.BREAK_BLOCKS;
            case NETHERITE_SWORD -> QuestType.KILL_ENTITIES;
            case OAK_LOG -> QuestType.PLACE_BLOCKS;
            case NETHERITE_INGOT -> QuestType.OBTAIN_ITEM;
            case EXPERIENCE_BOTTLE -> QuestType.GET_ACHIEVEMENT;
            default -> null;
        };
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if(!title.startsWith(typeTitle)) return;
        if(!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }
}
