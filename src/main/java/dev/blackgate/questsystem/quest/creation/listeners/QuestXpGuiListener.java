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

public class QuestXpGuiListener implements Listener {
    private final QuestSystem questSystem;
    private final String xpTitle;
    private boolean isSet;
    public QuestXpGuiListener(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.xpTitle = ChatColor.stripColor(questSystem.getConfigHelper().getQuestCreationMessage("set-xp"));
        this.isSet = false;
    }
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String title = event.getView().getTitle();
        if(title.equals(xpTitle) || title.endsWith(" levels")) {
            event.setCancelled(true);
            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            ItemMeta expBottleMeta = event.getView().getItem(4).getItemMeta();
            String expBottleName = ChatColor.stripColor(expBottleMeta.getDisplayName());
            int amount = Integer.parseInt(expBottleName.substring(0, expBottleName.indexOf(" ")));
            switch (itemName) {
                case "Remove 1 level" -> {
                    String levelsMessage = createMessage(amount-1);
                    expBottleMeta.setDisplayName(levelsMessage);
                    event.getView().setTitle(ChatColor.stripColor(levelsMessage));
                }
                case "Remove 10 levels" -> {
                    String levelsMessage = createMessage(amount-10);
                    expBottleMeta.setDisplayName(levelsMessage);
                    event.getView().setTitle(ChatColor.stripColor(levelsMessage));
                }
                case "Add 1 level" -> {
                    String levelsMessage = createMessage(amount+1);
                    expBottleMeta.setDisplayName(levelsMessage);
                    event.getView().setTitle(ChatColor.stripColor(levelsMessage));
                }
                case "Add 10 levels" -> {
                    String levelsMessage = createMessage(amount+10);
                    expBottleMeta.setDisplayName(levelsMessage);
                    event.getView().setTitle(ChatColor.stripColor(levelsMessage));
                }
                case "Finish" -> {
                    Player player = (Player) event.getWhoClicked();
                    finish(player, amount);
                }
                default -> {
                    return;
                }
            }
            ItemStack newExpBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
            newExpBottle.setItemMeta(expBottleMeta);
            event.getView().setItem(4, newExpBottle);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if(!title.equals(xpTitle) && !title.endsWith(" levels")) return;
        if(!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }


    private void finish(Player player, int amount) {
        isSet = true;
        QuestCreator creator = questSystem.getQuestCreationManager().getQuestCreator(player);
        creator.setXpAmount(amount);
        creator.openQuestRewardPrompt(QuestRewardType.COINS);
    }

    private String createMessage(int amount) {
        return ChatColor.GREEN + String.valueOf(amount) + " levels";
    }
}
