package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class QuestCreationListener implements Listener {
    QuestSystem questSystem;
    public QuestCreationListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        String title = questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace(" %stage%", "");
        title = ChatColor.stripColor(title);
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(!event.getView().getTitle().startsWith(title)) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        Player player = (Player) event.getWhoClicked();
        if(questSystem.getQuestCreationManager().getQuestCreator(player) == null) return;
        QuestCreator questCreator = questSystem.getQuestCreationManager().getQuestCreator(player);
        switch (event.getCurrentItem().getType()) {
            case NETHERITE_PICKAXE -> questCreator.setQuestType(QuestType.BREAK_BLOCKS);
            case NETHERITE_SWORD -> questCreator.setQuestType(QuestType.KILL_ENTITIES);
            case OAK_LOG -> questCreator.setQuestType(QuestType.PLACE_BLOCKS);
            case NETHERITE_INGOT -> questCreator.setQuestType(QuestType.OBTAIN_ITEM);
            case EXPERIENCE_BOTTLE -> questCreator.setQuestType(QuestType.GET_ACHIEVEMENT);
            default -> {
                return;
            }
        }

        event.setCancelled(true);
    }
}
