package com.fikjul.customweapon.token;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Storage untuk token balance players
 * Menyimpan data ke file YAML
 */
public class TokenStorage {
    
    private final CustomWeaponPlugin plugin;
    private final File storageFile;
    private FileConfiguration storage;
    
    // Cache untuk performance
    private final Map<UUID, Integer> balanceCache;

    public TokenStorage(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.storageFile = new File(plugin.getDataFolder(), "token-storage.yml");
        this.balanceCache = new HashMap<>();
        load();
    }

    /**
     * Load data dari file
     */
    private void load() {
        if (!storageFile.exists()) {
            try {
                storageFile.getParentFile().mkdirs();
                storageFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Gagal membuat token-storage.yml!");
                e.printStackTrace();
            }
        }

        storage = YamlConfiguration.loadConfiguration(storageFile);
        
        // Load ke cache
        if (storage.contains("balances")) {
            for (String key : storage.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int balance = storage.getInt("balances." + key, 0);
                    balanceCache.put(uuid, balance);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in storage: " + key);
                }
            }
        }

        plugin.getLogger().info("Loaded " + balanceCache.size() + " player token balances");
    }

    /**
     * Save data ke file
     */
    public void save() {
        // Save cache ke config
        for (Map.Entry<UUID, Integer> entry : balanceCache.entrySet()) {
            storage.set("balances." + entry.getKey().toString(), entry.getValue());
        }

        try {
            storage.save(storageFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Gagal menyimpan token-storage.yml!");
            e.printStackTrace();
        }
    }

    /**
     * Get balance player
     */
    public int getBalance(UUID uuid) {
        return balanceCache.getOrDefault(uuid, 0);
    }

    /**
     * Set balance player
     */
    public void setBalance(UUID uuid, int amount) {
        balanceCache.put(uuid, Math.max(0, amount));
    }

    /**
     * Add token ke balance
     */
    public void addBalance(UUID uuid, int amount) {
        int current = getBalance(uuid);
        setBalance(uuid, current + amount);
    }

    /**
     * Remove token dari balance
     * @return true jika berhasil
     */
    public boolean removeBalance(UUID uuid, int amount) {
        int current = getBalance(uuid);
        if (current < amount) {
            return false;
        }
        setBalance(uuid, current - amount);
        return true;
    }

    /**
     * Check apakah player punya cukup token
     */
    public boolean hasEnough(UUID uuid, int amount) {
        return getBalance(uuid) >= amount;
    }

    /**
     * Clear balance player
     */
    public void clearBalance(UUID uuid) {
        balanceCache.remove(uuid);
        storage.set("balances." + uuid.toString(), null);
    }

    /**
     * Get total tokens dari semua players
     */
    public long getTotalTokens() {
        return balanceCache.values().stream().mapToLong(Integer::longValue).sum();
    }

    /**
     * Get jumlah players yang punya token
     */
    public int getPlayersWithTokens() {
        return (int) balanceCache.values().stream().filter(balance -> balance > 0).count();
    }
}
