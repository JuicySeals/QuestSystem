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
import org.bukkit.inventory.meta.ItemMeta;

public class QuestCoinGuiListener implements Listener {
    private final QuestSystem questSystem;
    private final String coinTitle;
    private boolean isSet;
    public QuestCoinGuiListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.coinTitle = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("set-coins"));
        this.isSet = false;
    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String title = event.getView().getTitle();
        if(title.equals(coinTitle) || title.endsWith(" coins")) {
            event.setCancelled(true);
            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            ItemMeta goldIngotMeta = event.getView().getItem(4).getItemMeta();
            String goldIngotName = ChatColor.stripColor(goldIngotMeta.getDisplayName());
            int amount = Integer.parseInt(goldIngotName.substring(0, goldIngotName.indexOf(" ")));
            String coinMessage;
            switch (itemName) {
                case "Remove 1 coin" -> {
                    coinMessage = createMessage(amount-1);
                    goldIngotMeta.setDisplayName(coinMessage);
                    event.getView().setTitle(ChatColor.stripColor(coinMessage));
                }
                case "Remove 10 coins" -> {
                    coinMessage = createMessage(amount-10);
                    goldIngotMeta.setDisplayName(coinMessage);
                    event.getView().setTitle(ChatColor.stripColor(coinMessage));
                }
                case "Add 1 coin" -> {
                    coinMessage = createMessage(amount+1);
                    goldIngotMeta.setDisplayName(coinMessage);
                    event.getView().setTitle(ChatColor.stripColor(coinMessage));
                }
                case "Add 10 coins" -> {
                    coinMessage = createMessage(amount+10);
                    goldIngotMeta.setDisplayName(coinMessage);
                    event.getView().setTitle(ChatColor.stripColor(coinMessage));
                }
                case "Finish" -> {
                    Player player = (Player) event.getWhoClicked();
                    finish(player, amount);
                }
                default -> {
                    return;
                }
            }
            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);
            goldIngot.setItemMeta(goldIngotMeta);
            event.getView().setItem(4, goldIngot);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if(!title.equals(coinTitle) && !title.endsWith(" coins")) return;
        if(!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private void finish(Player player, int amount) {
        isSet = true;
        QuestCreator creator =  questSystem.getQuestCreationManager().getQuestCreator(player);
        creator.setCoinAmount(amount);
        creator.openQuestRewardPrompt(QuestRewardType.ITEMS);
    }

    private String createMessage(int amount) {
        return ChatColor.GREEN + String.valueOf(amount) + " coins";
    }
}
