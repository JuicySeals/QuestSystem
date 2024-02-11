package dev.blackgate.questsystem.util.inventory;

import dev.blackgate.questsystem.QuestSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemPDC {
    private final NamespacedKey namespacedKey;

    public ItemPDC(QuestSystem questSystem) {
        namespacedKey = new NamespacedKey(questSystem, "QuestSystem");
    }

    public void set(ItemStack itemStack, String value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        itemStack.setItemMeta(itemMeta);
    }

    public boolean isItem(ItemStack itemStack, String expectedValue) {
        if (!validate(itemStack)) return false;
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return persistentDataContainer.get(namespacedKey, PersistentDataType.STRING).equals(expectedValue);
    }

    public String getValue(ItemStack itemStack) {
        if (!validate(itemStack)) return null;
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return persistentDataContainer.get(namespacedKey, PersistentDataType.STRING);
    }

    private boolean validate(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return persistentDataContainer.has(namespacedKey);
    }
}
