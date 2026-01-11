package com.fikjul.customweapon.gacha;

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
 * System untuk mengelola pity counter untuk gacha
 * Soft pity & hard pity implementation
 */
public class PitySystem {
    
    private final CustomWeaponPlugin plugin;
    private final File pityFile;
    private FileConfiguration pityData;
    
    // Cache: PlayerUUID -> BannerName -> Counter
    private final Map<UUID, Map<String, Integer>> pityCounters;
    
    // Config values
    private final int hardPityGod;
    private final int hardPityMythic;
    private final int softPityStart;
    private final int softPityIncrement;
    private final boolean carryOver;
    private final boolean resetOnGod;
    private final boolean resetOnMythic;

    public PitySystem(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.pityFile = new File(plugin.getDataFolder(), "pity-data.yml");
        this.pityCounters = new HashMap<>();
        
        // Load config values
        FileConfiguration config = plugin.getConfigManager().getGachaConfig();
        this.hardPityGod = config.getInt("pity_system.guaranteed_god_tier", 90);
        this.hardPityMythic = config.getInt("pity_system.guaranteed_mythic_up", 50);
        this.softPityStart = config.getInt("pity_system.soft_pity_start", 70);
        this.softPityIncrement = config.getInt("pity_system.soft_pity_increment", 5);
        this.carryOver = config.getBoolean("pity_system.carry_over_between_banners", false);
        this.resetOnGod = config.getBoolean("pity_system.reset_on_god_tier", true);
        this.resetOnMythic = config.getBoolean("pity_system.reset_on_mythic", false);
        
        load();
    }

    /**
     * Load pity data dari file
     */
    private void load() {
        if (!pityFile.exists()) {
            try {
                pityFile.getParentFile().mkdirs();
                pityFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create pity-data.yml!");
                e.printStackTrace();
            }
        }

        pityData = YamlConfiguration.loadConfiguration(pityFile);
        
        // Load ke cache
        if (pityData.contains("pity")) {
            for (String uuidStr : pityData.getConfigurationSection("pity").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Map<String, Integer> bannerCounters = new HashMap<>();
                    
                    for (String banner : pityData.getConfigurationSection("pity." + uuidStr).getKeys(false)) {
                        int counter = pityData.getInt("pity." + uuidStr + "." + banner, 0);
                        bannerCounters.put(banner, counter);
                    }
                    
                    pityCounters.put(uuid, bannerCounters);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in pity data: " + uuidStr);
                }
            }
        }

        plugin.getLogger().info("Loaded pity data for " + pityCounters.size() + " players");
    }

    /**
     * Save pity data ke file
     */
    public void saveAll() {
        for (Map.Entry<UUID, Map<String, Integer>> entry : pityCounters.entrySet()) {
            String uuid = entry.getKey().toString();
            for (Map.Entry<String, Integer> banner : entry.getValue().entrySet()) {
                pityData.set("pity." + uuid + "." + banner.getKey(), banner.getValue());
            }
        }

        try {
            pityData.save(pityFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save pity-data.yml!");
            e.printStackTrace();
        }
    }

    /**
     * Get pity counter untuk player dan banner
     */
    public int getPityCounter(Player player, String banner) {
        return getPityCounter(player.getUniqueId(), banner);
    }

    public int getPityCounter(UUID uuid, String banner) {
        if (!pityCounters.containsKey(uuid)) {
            return 0;
        }
        return pityCounters.get(uuid).getOrDefault(banner, 0);
    }

    /**
     * Increment pity counter
     */
    public void incrementPity(Player player, String banner) {
        incrementPity(player.getUniqueId(), banner);
    }

    public void incrementPity(UUID uuid, String banner) {
        pityCounters.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> bannerCounters = pityCounters.get(uuid);
        int current = bannerCounters.getOrDefault(banner, 0);
        bannerCounters.put(banner, current + 1);
    }

    /**
     * Reset pity counter
     */
    public void resetPity(Player player, String banner) {
        resetPity(player.getUniqueId(), banner);
    }

    public void resetPity(UUID uuid, String banner) {
        if (pityCounters.containsKey(uuid)) {
            pityCounters.get(uuid).put(banner, 0);
        }
    }

    /**
     * Check apakah player di soft pity
     */
    public boolean isInSoftPity(Player player, String banner) {
        int counter = getPityCounter(player, banner);
        return counter >= softPityStart;
    }

    /**
     * Check apakah player di hard pity untuk God tier
     */
    public boolean isHardPityGod(Player player, String banner) {
        int counter = getPityCounter(player, banner);
        return counter >= hardPityGod;
    }

    /**
     * Check apakah player di hard pity untuk Mythic+
     */
    public boolean isHardPityMythic(Player player, String banner) {
        int counter = getPityCounter(player, banner);
        return counter >= hardPityMythic;
    }

    /**
     * Calculate bonus chance dari soft pity
     */
    public double getSoftPityBonus(Player player, String banner) {
        int counter = getPityCounter(player, banner);
        if (counter < softPityStart) {
            return 0.0;
        }
        
        int rollsAfterSoft = counter - softPityStart;
        return rollsAfterSoft * softPityIncrement / 100.0; // Convert to decimal
    }

    /**
     * Handle pity setelah roll based on rarity
     */
    public void handlePityAfterRoll(Player player, String banner, String rarity) {
        if (rarity.equalsIgnoreCase("GOD") && resetOnGod) {
            resetPity(player, banner);
        } else if (rarity.equalsIgnoreCase("MYTHIC") && resetOnMythic) {
            resetPity(player, banner);
        } else {
            incrementPity(player, banner);
        }
    }

    /**
     * Get statistics untuk player
     */
    public Map<String, Integer> getPlayerPityStats(Player player) {
        UUID uuid = player.getUniqueId();
        if (!pityCounters.containsKey(uuid)) {
            return new HashMap<>();
        }
        return new HashMap<>(pityCounters.get(uuid));
    }

    // Getters for config values
    public int getHardPityGod() {
        return hardPityGod;
    }

    public int getHardPityMythic() {
        return hardPityMythic;
    }

    public int getSoftPityStart() {
        return softPityStart;
    }
}
