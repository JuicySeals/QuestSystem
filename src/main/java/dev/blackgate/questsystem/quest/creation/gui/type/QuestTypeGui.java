package dev.blackgate.questsystem.quest.creation.gui.type;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import dev.blackgate.questsystem.quest.enums.QuestType;
import dev.blackgate.questsystem.util.Formatter;
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

public class QuestTypeGui implements InventoryGUI {
    private Inventory inventory;
    private ConfigHelper configHelper;
    private QuestSystem questSystem;
    private boolean isSet;

    public QuestTypeGui(QuestSystem questSystem) {
        this.configHelper = questSystem.getConfigHelper();
        this.questSystem = questSystem;
        this.isSet = false;
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
        ItemStack getAchievment = new ItemStack(Material.EXPERIENCE_BOTTLE);
        items.add(breakBlocks);
        items.add(killEntities);
        items.add(placeBlocks);
        items.add(obtainItems);
        items.add(getAchievment);
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
        return switch (item.getType()) {
            case NETHERITE_PICKAXE -> QuestType.BREAK_BLOCKS;
            case NETHERITE_SWORD -> QuestType.KILL_ENTITIES;
            case OAK_LOG -> QuestType.PLACE_BLOCKS;
            case NETHERITE_INGOT -> QuestType.OBTAIN_ITEM;
            case EXPERIENCE_BOTTLE -> QuestType.GET_ACHIEVEMENT;
            default -> null;
        };
    }
}
