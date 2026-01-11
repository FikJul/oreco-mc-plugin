package com.fikjul.customweapon.gacha;

import com.fikjul.customweapon.weapon.WeaponRarity;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents a gacha banner with drop pools and rates
 */
public class GachaBanner {
    
    private final String id;
    private final String displayName;
    private final Material icon;
    private final boolean enabled;
    private final int costSingle;
    private final int costMulti;
    
    // Drop rates (total should be 100)
    private final Map<WeaponRarity, Double> dropRates;
    
    // Drop pools per rarity
    private final Map<WeaponRarity, List<DropItem>> dropPools;

    public GachaBanner(String id, ConfigurationSection section) {
        this.id = id;
        this.displayName = section.getString("display_name", id);
        this.icon = Material.getMaterial(section.getString("icon", "NETHERITE_SWORD"));
        this.enabled = section.getBoolean("enabled", false);
        this.costSingle = section.getInt("cost_single", 1);
        this.costMulti = section.getInt("cost_multi", 9);
        
        this.dropRates = new HashMap<>();
        this.dropPools = new HashMap<>();
        
        loadDropRates(section);
        loadDropPools(section);
    }

    /**
     * Load drop rates dari config
     */
    private void loadDropRates(ConfigurationSection section) {
        ConfigurationSection ratesSection = section.getConfigurationSection("drop_rates");
        if (ratesSection != null) {
            for (String rarityStr : ratesSection.getKeys(false)) {
                try {
                    WeaponRarity rarity = WeaponRarity.fromString(rarityStr);
                    double rate = ratesSection.getDouble(rarityStr, 0.0);
                    dropRates.put(rarity, rate);
                } catch (Exception e) {
                    // Invalid rarity, skip
                }
            }
        }
    }

    /**
     * Load drop pools dari config
     */
    private void loadDropPools(ConfigurationSection section) {
        ConfigurationSection poolSection = section.getConfigurationSection("drop_pool");
        if (poolSection == null) return;

        for (String rarityStr : poolSection.getKeys(false)) {
            try {
                WeaponRarity rarity = WeaponRarity.fromString(rarityStr);
                List<DropItem> items = new ArrayList<>();
                
                List<?> itemsList = poolSection.getList(rarityStr);
                if (itemsList != null) {
                    for (Object obj : itemsList) {
                        if (obj instanceof Map) {
                            Map<?, ?> itemMap = (Map<?, ?>) obj;
                            items.add(parseDropItem(itemMap));
                        }
                    }
                }
                
                dropPools.put(rarity, items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parse drop item dari map
     */
    private DropItem parseDropItem(Map<?, ?> map) {
        DropItem item = new DropItem();
        
        // Check if it's a weapon
        if (map.containsKey("weapon")) {
            item.type = DropItemType.WEAPON;
            item.weaponId = (String) map.get("weapon");
            item.weight = map.containsKey("weight") ? ((Number) map.get("weight")).intValue() : 1;
            return item;
        }
        
        // Otherwise it's a material
        item.type = DropItemType.MATERIAL;
        item.material = Material.getMaterial((String) map.get("material"));
        item.amount = map.containsKey("amount") ? ((Number) map.get("amount")).intValue() : 1;
        item.weight = map.containsKey("weight") ? ((Number) map.get("weight")).intValue() : 1;
        item.customName = (String) map.get("name");
        
        // Parse enchantments
        if (map.containsKey("enchants") && map.get("enchants") instanceof Map) {
            Map<?, ?> enchantsMap = (Map<?, ?>) map.get("enchants");
            item.enchantments = new HashMap<>();
            for (Map.Entry<?, ?> entry : enchantsMap.entrySet()) {
                Enchantment enchant = Enchantment.getByName(((String) entry.getKey()).toUpperCase());
                if (enchant != null) {
                    item.enchantments.put(enchant, ((Number) entry.getValue()).intValue());
                }
            }
        }
        
        return item;
    }

    /**
     * Get random drop item based on rarity
     */
    public DropItem getRandomDrop(WeaponRarity rarity, Random random) {
        List<DropItem> pool = dropPools.get(rarity);
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        
        // Weighted random selection
        int totalWeight = pool.stream().mapToInt(item -> item.weight).sum();
        int randomWeight = random.nextInt(totalWeight);
        
        int currentWeight = 0;
        for (DropItem item : pool) {
            currentWeight += item.weight;
            if (randomWeight < currentWeight) {
                return item;
            }
        }
        
        return pool.get(0); // Fallback
    }

    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }
    public boolean isEnabled() { return enabled; }
    public int getCostSingle() { return costSingle; }
    public int getCostMulti() { return costMulti; }
    public Map<WeaponRarity, Double> getDropRates() { return dropRates; }
    public Map<WeaponRarity, List<DropItem>> getDropPools() { return dropPools; }

    /**
     * Drop item representation
     */
    public static class DropItem {
        public DropItemType type;
        public Material material;
        public int amount = 1;
        public int weight = 1;
        public String customName;
        public Map<Enchantment, Integer> enchantments;
        public String weaponId;
    }

    public enum DropItemType {
        MATERIAL,
        WEAPON
    }
}
