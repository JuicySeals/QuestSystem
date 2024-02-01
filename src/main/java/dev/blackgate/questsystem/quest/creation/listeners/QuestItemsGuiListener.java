package dev.blackgate.questsystem.quest.creation.listeners;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class QuestItemsGuiListener implements Listener {
    private final QuestSystem questSystem;
    private final String itemTitle;
    private boolean isSet;
    public QuestItemsGuiListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.itemTitle = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("place-items"));
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)
                || event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
            return;
        }

        String title = event.getView().getTitle();
        if(!title.equals(itemTitle)) {
            return;
        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Finish")) {
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());
            items = filterItems(items);
            finish((Player) event.getWhoClicked(), items);
        }
        event.setCancelled(true);
    }

    private List<ItemStack> filterItems(List<ItemStack> items) {
        List<ItemStack> updatedItems = new ArrayList<>(items);

        Iterator<ItemStack> itemStackIterator = updatedItems.iterator();
        while (itemStackIterator.hasNext()) {
            ItemStack item = itemStackIterator.next();
            if(item == null) {
                itemStackIterator.remove();
                continue;
            }
            if (item.getType() != Material.BAMBOO || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                continue;
            }
            if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Finish")) {
                itemStackIterator.remove();
            }
        }
        return updatedItems;
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        if(!event.getView().getTitle().equals(itemTitle)) return;
        if(!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private void finish(Player player, List<ItemStack> items) {
        isSet = true;
        QuestCreator creator = questSystem.getQuestCreationManager().getQuestCreator(player);
        creator.setItems(items);
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("added-items").replace("%value%", String.valueOf(items.size())));
        creator.openQuestRewardPrompt(QuestRewardType.COMMAND);
        player.closeInventory();
    }
}
