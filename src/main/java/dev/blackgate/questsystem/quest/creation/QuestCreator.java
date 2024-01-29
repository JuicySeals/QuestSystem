package dev.blackgate.questsystem.quest.creation;

import de.rapha149.signgui.SignGUI;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.enums.QuestRewardType;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestCreator {
    private Player player;
    private QuestSystem questSystem;
    private String questName, description;
    private QuestType questType;
    private QuestRewardType questRewardType;
    public QuestCreator(Player player, QuestSystem questSystem) {
        this.player = player;
        this.questSystem = questSystem;
        questSystem.getQuestCreationManager().addPlayer(player, this);
        openNamePrompt();
    }

    private void openQuestTypeGui() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace("%stage%", "type");
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.stripColor(message));
        List<ItemStack> items = getQuestTypeItems();
        for(int i = 0; i < items.size(); i++) {
            inv.setItem(i+2, items.get(i));
        }
        player.sendMessage(message);
        player.openInventory(inv);
    }

    private void openNamePrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-details").replace("%stage%", "name");
        player.sendMessage(message);
        SignGUI signGUI = SignGUI.builder().setHandler((p, result) -> {
            String[] nameArray = result.getLines();
            setName(String.join("", nameArray));
            new BukkitRunnable() {
                @Override
                public void run() {
                    openDescriptionPrompt();
                }
            }.runTask(questSystem);
            return Collections.emptyList();
        }).build();
        signGUI.open(player);
    }

    private void openDescriptionPrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("input-details").replace("%stage%", "description");
        player.sendMessage(message);
        SignGUI signGUI = SignGUI.builder()
                .setHandler((p, result) -> {
                    String[] descriptionArray = result.getLines();
                    setDescription(String.join("", descriptionArray));
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            openQuestTypeGui();
                        }
                    }.runTask(questSystem);
                    return Collections.emptyList();
                }).build();
        signGUI.open(player);
    }

    public void setName(String name) {
        this.questName = name;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "name").replace("%value%", name);
        player.sendMessage(message);
    }

    public void setDescription(String description) {
        this.description = description;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "description").replace("%value%", description);
        player.sendMessage(message);
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
        String message = questSystem.getConfigHelper().getQuestCreationMessage("set-details").replace("%stage%", "quest type").replace("%value%", formatEnumName(questType));
        player.sendMessage(message);
        openRewardTypePrompt();
    }

    private void openRewardTypePrompt() {
        String message = questSystem.getConfigHelper().getQuestCreationMessage("select-type").replace("%stage%", "reward type");
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.stripColor(message));
        List<ItemStack> items = getQuestRewardItems();
        for(int i = 0; i < items.size(); i++) {
            inv.setItem(i+2, items.get(i));
        }
        player.openInventory(inv);
        //TODO CREATE METHOD TO SET REWARD TYPE AND ADD TO LISTENER TO HANDLE WHICH ONE IS CLICKED
        player.sendMessage(message);

    }

    private List<ItemStack> getQuestTypeItems() {
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
        for(int i = 0; i < 5; i++) {
            String name = formatEnumName(QuestType.values()[i]);
            ItemMeta meta = items.get(i).getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            items.get(i).setItemMeta(meta);
        }
        return items;
    }

    private List<ItemStack> getQuestRewardItems() {
        List<ItemStack> items = new ArrayList<>();
        ItemStack command = new ItemStack(Material.COMMAND_BLOCK);
        ItemStack item = new ItemStack(Material.NETHERITE_INGOT);
        ItemStack coins = new ItemStack(Material.GOLD_INGOT);
        ItemStack xp = new ItemStack(Material.EXPERIENCE_BOTTLE);
        items.add(command);
        items.add(item);
        items.add(coins);
        items.add(xp);
        for(int i = 0; i < 4; i++) {
            String name = formatEnumName(QuestRewardType.values()[i]);
            ItemMeta meta = items.get(i).getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            items.get(i).setItemMeta(meta);
        }
        return items;
    }

    private String formatEnumName(Enum<?> type) {
        String name = type.name();
        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        return name;
    }
}
