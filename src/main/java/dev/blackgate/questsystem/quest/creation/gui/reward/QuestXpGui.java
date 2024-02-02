package dev.blackgate.questsystem.quest.creation.gui.reward;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.inventory.InventoryGUI;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestXpGui implements InventoryGUI {
    private final ConfigHelper configHelper;
    private final QuestSystem questSystem;
    private Inventory inventory;
    private boolean isSet;

    public QuestXpGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.questSystem = questSystem;
        this.isSet = false;
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, ChatColor.stripColor(configHelper.getQuestCreationMessage("set-xp")));
        List<ItemStack> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == Material.BAMBOO) {
                inventory.setItem(8, items.get(i));
                continue;
            }
            inventory.setItem(i + 2, items.get(i));
        }
        questSystem.getInventoryManager().registerHandledInventory(inventory, this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        ItemStack remove10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.RED + "Remove 10 levels");
        ItemStack remove1 = createButton(Material.STONE_BUTTON, ChatColor.RED + "Remove 1 level");
        ItemStack expBottle = createButton(Material.EXPERIENCE_BOTTLE, ChatColor.GREEN + "0 levels");
        ItemStack add1 = createButton(Material.STONE_BUTTON, ChatColor.GREEN + "Add 1 level");
        ItemStack add10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.GREEN + "Add 10 levels");
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(ChatColor.GREEN + "Finish");
        finish.setItemMeta(finishMeta);

        items.add(remove10);
        items.add(remove1);
        items.add(expBottle);
        items.add(add1);
        items.add(add10);
        items.add(finish);

        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String title = event.getView().getTitle();
        event.setCancelled(true);
        String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        ItemMeta expBottleMeta = event.getView().getItem(4).getItemMeta();
        String expBottleName = ChatColor.stripColor(expBottleMeta.getDisplayName());
        int amount = Integer.parseInt(expBottleName.substring(0, expBottleName.indexOf(" ")));
        switch (itemName) {
            case "Remove 1 level" -> {
                String levelsMessage = createMessage(amount - 1);
                expBottleMeta.setDisplayName(levelsMessage);
                event.getView().setTitle(ChatColor.stripColor(levelsMessage));
            }
            case "Remove 10 levels" -> {
                String levelsMessage = createMessage(amount - 10);
                expBottleMeta.setDisplayName(levelsMessage);
                event.getView().setTitle(ChatColor.stripColor(levelsMessage));
            }
            case "Add 1 level" -> {
                String levelsMessage = createMessage(amount + 1);
                expBottleMeta.setDisplayName(levelsMessage);
                event.getView().setTitle(ChatColor.stripColor(levelsMessage));
            }
            case "Add 10 levels" -> {
                String levelsMessage = createMessage(amount + 10);
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

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private ItemStack createButton(Material material, String displayName) {
        ItemStack button = new ItemStack(material);
        ItemMeta buttonMeta = button.getItemMeta();
        buttonMeta.setDisplayName(displayName);
        button.setItemMeta(buttonMeta);
        return button;
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
