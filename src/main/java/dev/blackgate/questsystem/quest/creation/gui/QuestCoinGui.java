package dev.blackgate.questsystem.quest.creation.gui;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.util.InventoryGUI;
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

public class QuestCoinGui implements InventoryGUI {
    private final ConfigHelper configHelper;
    private Inventory inventory;
    private boolean isSet;
    private QuestSystem questSystem;

    public QuestCoinGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.isSet = false;
        this.questSystem = questSystem;
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, ChatColor.stripColor(configHelper.getQuestCreationMessage("set-coins")));
        List<ItemStack> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            // Bamboo stick looks better at the end
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
        player.openInventory(getInventory());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        ItemStack remove10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.RED + "Remove 10 coins");
        ItemStack remove1 = createButton(Material.STONE_BUTTON, ChatColor.RED + "Remove 1 coin");
        ItemStack goldIngot = createButton(Material.GOLD_INGOT, ChatColor.GREEN + "0 coins");
        ItemStack add1 = createButton(Material.STONE_BUTTON, ChatColor.GREEN + "Add 1 coin");
        ItemStack add10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.GREEN + "Add 10 coins");
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(ChatColor.GREEN + "Finish");
        finish.setItemMeta(finishMeta);

        items.add(remove10);
        items.add(remove1);
        items.add(goldIngot);
        items.add(add1);
        items.add(add10);
        items.add(finish);

        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String title = event.getView().getTitle();
        event.setCancelled(true);
        String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        ItemMeta goldIngotMeta = event.getView().getItem(4).getItemMeta();
        String goldIngotName = ChatColor.stripColor(goldIngotMeta.getDisplayName());
        int amount = Integer.parseInt(goldIngotName.substring(0, goldIngotName.indexOf(" ")));
        String coinMessage;
        switch (itemName) {
            case "Remove 1 coin" -> {
                coinMessage = createMessage(amount - 1);
                goldIngotMeta.setDisplayName(coinMessage);
                event.getView().setTitle(ChatColor.stripColor(coinMessage));
            }
            case "Remove 10 coins" -> {
                coinMessage = createMessage(amount - 10);
                goldIngotMeta.setDisplayName(coinMessage);
                event.getView().setTitle(ChatColor.stripColor(coinMessage));
            }
            case "Add 1 coin" -> {
                coinMessage = createMessage(amount + 1);
                goldIngotMeta.setDisplayName(coinMessage);
                event.getView().setTitle(ChatColor.stripColor(coinMessage));
            }
            case "Add 10 coins" -> {
                coinMessage = createMessage(amount + 10);
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
        creator.setCoinAmount(amount);
        creator.openQuestRewardPrompt(QuestRewardType.ITEMS);
    }

    private String createMessage(int amount) {
        return ChatColor.GREEN + String.valueOf(amount) + " coins";
    }
}
