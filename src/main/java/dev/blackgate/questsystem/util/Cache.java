package dev.blackgate.questsystem.util;

import dev.blackgate.questsystem.QuestSystem;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Cache<K, V> {
    private final HashMap<K, V> cacheMap;
    private final QuestSystem questSystem;

    public Cache(QuestSystem questSystem) {
        cacheMap = new HashMap<>();
        this.questSystem = questSystem;
    }

    public void addValue(K key, V value) {
        cacheMap.put(key, value);
        startCacheDecay(key);
    }

    public V getValue(K key) {
        return cacheMap.get(key);
    }

    public boolean containsKey(K key) {
        return cacheMap.containsKey(key);
    }

    public void removeFromCache(K key) {
        cacheMap.remove(key);
    }

    private void startCacheDecay(K key) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeFromCache(key);
            }
        }.runTaskLater(questSystem, 600);
    }
}
