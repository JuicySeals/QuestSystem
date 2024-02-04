package dev.blackgate.questsystem.util.inventory.types.item;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import dev.blackgate.questsystem.util.inventory.InventoryGUI;
import dev.blackgate.questsystem.util.inventory.ItemPDC;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ItemsGui implements InventoryGUI {
    private final ConfigHelper configHelper;
    private static final String BUTTON_NAME = ChatColor.GREEN + "Finish";
    private Inventory inventory;
    private boolean isSet;
    private final QuestSystem questSystem;
    private ItemsGuiHandler itemsGuiHandler;
    private final ItemPDC itemPDC;

    public ItemsGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.isSet = false;
        this.questSystem = questSystem;
        this.itemPDC = questSystem.getItemPDC();
        create();
    }

    public void setHandler(ItemsGuiHandler itemsGuiHandler) {
        this.itemsGuiHandler = itemsGuiHandler;
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
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(BUTTON_NAME);
        finish.setItemMeta(finishMeta);

        questSystem.getItemPDC().set(finish, "FINISH");

        items.add(finish);
        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (itemPDC.isItem(event.getCurrentItem(), "FINISH")) {
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());
            items = filterItems(items);
            finish((Player) event.getWhoClicked(), items);
            event.setCancelled(true);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 54, ChatColor.stripColor(configHelper.getQuestCreationMessage("place-items")));
        inventory.setItem(53, getItems().get(0));
        questSystem.getInventoryManager().registerHandledInventory(inventory, this);
    }

    private void finish(Player player, List<ItemStack> items) {
        isSet = true;
        player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("added-items").replace("%value%", String.valueOf(items.size())));
        player.closeInventory();
        itemsGuiHandler.onFinish(items, questSystem.getQuestCreationManager().getQuestCreator(player));
    }

    public List<ItemStack> filterItems(List<ItemStack> items) {
        List<ItemStack> updatedItems = new ArrayList<>(items);

        updatedItems.removeIf(item -> item == null
                || (item.getType() != Material.BAMBOO)
                || (!item.hasItemMeta())
                || (!item.getItemMeta().hasDisplayName())
                || (!item.getItemMeta().getDisplayName().equals(BUTTON_NAME)));
        return updatedItems;
    }
}