package dev.blackgate.questsystem.util.inventory.types.numberinput;

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

import java.util.ArrayList;
import java.util.List;

public class NumberInputGui implements InventoryGUI {
    private Inventory inventory;
    private boolean isSet;
    private final QuestSystem questSystem;
    private final ItemPDC itemPDC;
    private final ItemStack centerItem;
    private final String numberType;
    private NumberInputHandler numberInputHandler;

    public NumberInputGui(QuestSystem questSystem, ItemStack centerItem, String numberType) {
        this.isSet = false;
        this.questSystem = questSystem;
        this.itemPDC = questSystem.getItemPDC();
        this.centerItem = centerItem;
        this.numberType = numberType;
        create();
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, "Input amount");
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

        ItemStack remove10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.RED + "Remove 10 " + numberType + "s");
        ItemStack remove1 = createButton(Material.STONE_BUTTON, ChatColor.RED + "Remove 1 " + numberType);
        ItemStack icon = createButton(centerItem.getType(), ChatColor.GREEN + "0 " + numberType);
        ItemStack add1 = createButton(Material.STONE_BUTTON, ChatColor.GREEN + "Add 1 " + numberType);
        ItemStack add10 = createButton(Material.POLISHED_BLACKSTONE_BUTTON, ChatColor.GREEN + "Add 10 " + numberType + "s");
        ItemStack finish = new ItemStack(Material.BAMBOO);
        ItemMeta finishMeta = finish.getItemMeta();
        finishMeta.setDisplayName(ChatColor.GREEN + "Finish");
        finish.setItemMeta(finishMeta);

        itemPDC.set(remove10, "REMOVE_10");
        itemPDC.set(remove1, "REMOVE_1");
        itemPDC.set(add1, "ADD_1");
        itemPDC.set(add10, "ADD_10");
        itemPDC.set(finish, "FINISH");

        items.add(remove10);
        items.add(remove1);
        items.add(icon);
        items.add(add1);
        items.add(add10);
        items.add(finish);

        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        ItemMeta iconMeta = event.getView().getItem(4).getItemMeta();
        String iconName = ChatColor.stripColor(iconMeta.getDisplayName());
        int amount = Integer.parseInt(iconName.substring(0, iconName.indexOf(" ")));
        String typeMessage;
        if(itemPDC.getValue(event.getCurrentItem()) == null) return;
        switch (itemPDC.getValue(event.getCurrentItem())) {
            case "REMOVE_1" -> {
                typeMessage = createMessage(amount - 1);
                iconMeta.setDisplayName(typeMessage);
                event.getView().setTitle(ChatColor.stripColor(typeMessage));
            }
            case "REMOVE_10" -> {
                typeMessage = createMessage(amount - 10);
                iconMeta.setDisplayName(typeMessage);
                event.getView().setTitle(ChatColor.stripColor(typeMessage));
            }
            case "ADD_1" -> {
                typeMessage = createMessage(amount + 1);
                iconMeta.setDisplayName(typeMessage);
                event.getView().setTitle(ChatColor.stripColor(typeMessage));
            }
            case "ADD_10" -> {
                typeMessage = createMessage(amount + 10);
                iconMeta.setDisplayName(typeMessage);
                event.getView().setTitle(ChatColor.stripColor(typeMessage));
            }
            case "FINISH" -> finish((Player) event.getWhoClicked(), amount);
            default -> {
                return;
            }
        }
        centerItem.setItemMeta(iconMeta);
        event.getView().setItem(4, centerItem);
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
        numberInputHandler.onFinish(player, amount);
    }

    private String createMessage(int amount) {
        if(amount > 1) {
            return ChatColor.GREEN + String.valueOf(amount) + " " + numberType + "s";
        }else {
            return ChatColor.GREEN + String.valueOf(amount) + " " + numberType;
        }
    }

    public void setHandler(NumberInputHandler numberInputHandler) {
        this.numberInputHandler = numberInputHandler;
    }
}
