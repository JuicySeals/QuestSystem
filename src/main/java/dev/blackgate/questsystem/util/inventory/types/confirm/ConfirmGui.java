package dev.blackgate.questsystem.util.inventory.types.confirm;

import dev.blackgate.questsystem.QuestSystem;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ConfirmGui implements InventoryGUI {
    private final QuestSystem questSystem;
    private final ItemPDC itemPDC;
    private Inventory inventory;
    private static final String CANCEL_BUTTON = "CANCEL"; // Constants should always be static (If not it implies the name can change in other classes)
    private static final String CONFIRM_BUTTON = "CONFIRM"; // Constants should always be static (If not it implies the name can change in other classes)
    private ConfirmGuiHandler confirmGuiHandler;
    private boolean isSet;

    public ConfirmGui(QuestSystem questSystem) {
        this.questSystem = questSystem;
        this.itemPDC = questSystem.getItemPDC();
        this.isSet = false;
        createInventory();
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
        ItemStack cancel = createItem(Material.RED_CONCRETE,  ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Cancel"); // Cant combine 2 chatcolors have to use String.valueOf()
        ItemStack confirm = createItem(Material.GREEN_CONCRETE, ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "Confirm"); // Cant combine 2 chatcolors have to use String.valueOf()

        itemPDC.set(cancel, CANCEL_BUTTON);
        itemPDC.set(confirm, CONFIRM_BUTTON);

        items.add(cancel);
        items.add(confirm);
        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (itemPDC.isItem(currentItem, CANCEL_BUTTON) || itemPDC.isItem(currentItem, CONFIRM_BUTTON)) {
            if (itemPDC.isItem(currentItem, CANCEL_BUTTON)) {
                cancel(player);
                closeInventorySync(player);
                confirmGuiHandler.onFinish(false, player);
                isSet = true;
            } else {
                closeInventorySync(player);
                confirmGuiHandler.onFinish(true, player);
            }
        }
    }

    private void closeInventorySync(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTask(questSystem);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if(isSet) return;
        cancel((Player) event.getPlayer());
    }

    private void cancel(Player player) {
        String message = questSystem.getConfigHelper().getString("confirm-gui.cancel-message");
        player.sendMessage(message);
    }

    private void createInventory() {
        inventory = Bukkit.createInventory(null, 27, "Are you sure?");
        inventory.setItem(12, getItems().get(0)); // Cancel item
        inventory.setItem(14, getItems().get(1)); // Confirm item
    }

    private ItemStack createItem(Material material, String displayName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void setHandler(ConfirmGuiHandler confirmGuiHandler) {
        this.confirmGuiHandler = confirmGuiHandler;
    }
}