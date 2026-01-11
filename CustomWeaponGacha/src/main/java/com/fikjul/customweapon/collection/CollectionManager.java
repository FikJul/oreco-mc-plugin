package com.fikjul.customweapon.collection;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manager untuk collection system (weapon tracking)
 */
public class CollectionManager {
    
    private final CustomWeaponPlugin plugin;
    private final File collectionFile;
    private FileConfiguration collectionData;
    
    // Cache: PlayerUUID -> Set of collected weapon IDs
    private final Map<UUID, Set<String>> collections;

    public CollectionManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.collectionFile = new File(plugin.getDataFolder(), "collections.yml");
        this.collections = new HashMap<>();
        load();
    }

    /**
     * Load collection data
     */
    private void load() {
        if (!collectionFile.exists()) {
            try {
                collectionFile.getParentFile().mkdirs();
                collectionFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create collections.yml!");
                e.printStackTrace();
            }
        }

        collectionData = YamlConfiguration.loadConfiguration(collectionFile);
        
        // Load to cache
        if (collectionData.contains("collections")) {
            for (String uuidStr : collectionData.getConfigurationSection("collections").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> weaponIds = collectionData.getStringList("collections." + uuidStr);
                    collections.put(uuid, new HashSet<>(weaponIds));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in collections: " + uuidStr);
                }
            }
        }

        plugin.getLogger().info("Loaded collections for " + collections.size() + " players");
    }

    /**
     * Save collection data
     */
    public void saveAll() {
        for (Map.Entry<UUID, Set<String>> entry : collections.entrySet()) {
            collectionData.set("collections." + entry.getKey().toString(), 
                    new ArrayList<>(entry.getValue()));
        }

        try {
            collectionData.save(collectionFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save collections.yml!");
            e.printStackTrace();
        }
    }

    /**
     * Add weapon to player collection
     * @return true if it's a new weapon
     */
    public boolean addWeapon(Player player, String weaponId) {
        return addWeapon(player.getUniqueId(), weaponId);
    }

    public boolean addWeapon(UUID uuid, String weaponId) {
        collections.putIfAbsent(uuid, new HashSet<>());
        return collections.get(uuid).add(weaponId);
    }

    /**
     * Check if player has weapon in collection
     */
    public boolean hasWeapon(Player player, String weaponId) {
        return hasWeapon(player.getUniqueId(), weaponId);
    }

    public boolean hasWeapon(UUID uuid, String weaponId) {
        if (!collections.containsKey(uuid)) {
            return false;
        }
        return collections.get(uuid).contains(weaponId);
    }

    /**
     * Get player collection
     */
    public Set<String> getCollection(Player player) {
        return getCollection(player.getUniqueId());
    }

    public Set<String> getCollection(UUID uuid) {
        return new HashSet<>(collections.getOrDefault(uuid, new HashSet<>()));
    }

    /**
     * Get collection count
     */
    public int getCollectionCount(Player player) {
        return getCollection(player).size();
    }

    /**
     * Get total weapons available
     */
    public int getTotalWeapons() {
        return plugin.getWeaponRegistry().getLoadedWeaponCount();
    }

    /**
     * Get collection completion percentage
     */
    public double getCompletionPercentage(Player player) {
        int total = getTotalWeapons();
        if (total == 0) return 0.0;
        
        int collected = getCollectionCount(player);
        return (collected * 100.0) / total;
    }
}
