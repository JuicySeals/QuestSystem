package dev.blackgate.questsystem.quest.creation.gui.type;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Formatter;
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
import java.util.List;

public class QuestTypeGui implements InventoryGUI {
    private Inventory inventory;
    private final ConfigHelper configHelper;
    private final QuestSystem questSystem;
    private boolean isSet;
    private final ItemPDC itemPDC;

    public QuestTypeGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.questSystem = questSystem;
        this.isSet = false;
        this.itemPDC = questSystem.getItemPDC();
        create();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(getInventory());
    }

    private void create() {
        inventory = Bukkit.createInventory(null, 9, ChatColor.stripColor(configHelper.getQuestCreationMessage("select-type")));
        List<ItemStack> items = getItems();
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i + 2, items.get(i));
        }
        questSystem.getInventoryManager().registerHandledInventory(inventory, this);
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        ItemStack breakBlocks = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemStack killEntities = new ItemStack(Material.NETHERITE_SWORD);
        ItemStack placeBlocks = new ItemStack(Material.OAK_LOG);
        ItemStack obtainItems = new ItemStack(Material.NETHERITE_INGOT);
        ItemStack getAchievement = new ItemStack(Material.EXPERIENCE_BOTTLE);

        itemPDC.set(breakBlocks, "BREAK_BLOCKS");
        itemPDC.set(killEntities, "KILL_ENTITIES");
        itemPDC.set(placeBlocks, "PLACE_BLOCKS");
        itemPDC.set(obtainItems, "OBTAIN_ITEMS");
        itemPDC.set(getAchievement, "GET_ACHIEVEMENT");

        items.add(breakBlocks);
        items.add(killEntities);
        items.add(placeBlocks);
        items.add(obtainItems);
        items.add(getAchievement);
        for (int i = 0; i < 5; i++) {
            String name = Formatter.formatEnumName(QuestType.values()[i]);
            ItemMeta meta = items.get(i).getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            items.get(i).setItemMeta(meta);
        }
        return items;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null) {
            return;
        }

        QuestCreator questCreator = questSystem.getQuestCreationManager().getQuestCreator(player);

        if (questCreator == null) {
            return;
        }
        isSet = true;
        questCreator.setQuestType(getQuestTypeFromItem(event.getCurrentItem()));
    }


    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!isSet) {
            questSystem.getQuestCreationManager().removeQuestCreator(player);
            player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
        }
    }

    private QuestType getQuestTypeFromItem(ItemStack item) {
        if(itemPDC.getValue(item) == null) return null;
        return switch (itemPDC.getValue(item)) {
            case "BREAK_BLOCKS" -> QuestType.BREAK_BLOCKS;
            case "KILL_ENTITIES" -> QuestType.KILL_ENTITIES;
            case "PLACE_BLOCKS" -> QuestType.PLACE_BLOCKS;
            case "OBTAIN_ITEMS" -> QuestType.OBTAIN_ITEM;
            case "GET_ACHIEVEMENT" -> QuestType.GET_ACHIEVEMENT;
            default -> null;
        };
    }
}
