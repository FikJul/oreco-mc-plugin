package com.fikjul.customweapon.weapon;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Registry untuk menyimpan dan mengelola custom weapons
 */
public class WeaponRegistry {
    
    private final CustomWeaponPlugin plugin;
    private final Map<String, CustomWeapon> weapons;
    private final NamespacedKey weaponKey;

    public WeaponRegistry(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.weapons = new HashMap<>();
        this.weaponKey = new NamespacedKey(plugin, "custom_weapon_id");
    }

    /**
     * Load semua weapons dari config
     */
    public void loadWeapons() {
        weapons.clear();
        
        FileConfiguration weaponsConfig = plugin.getConfigManager().getWeaponsConfig();
        ConfigurationSection weaponsSection = weaponsConfig.getConfigurationSection("weapons");

        if (weaponsSection == null) {
            plugin.getLogger().warning("No weapons found in weapons.yml!");
            return;
        }

        int loaded = 0;
        for (String weaponId : weaponsSection.getKeys(false)) {
            try {
                CustomWeapon weapon = loadWeapon(weaponId, weaponsSection.getConfigurationSection(weaponId));
                if (weapon != null) {
                    weapons.put(weaponId, weapon);
                    loaded++;
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load weapon: " + weaponId);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " custom weapons successfully!");
    }

    /**
     * Load single weapon dari config section
     */
    private CustomWeapon loadWeapon(String id, ConfigurationSection section) {
        if (section == null) return null;

        CustomWeapon.Builder builder = new CustomWeapon.Builder(id);

        // Basic properties
        builder.displayName(section.getString("display_name", id));
        builder.lore(section.getStringList("lore"));
        
        String baseMaterial = section.getString("base_item", "NETHERITE_SWORD");
        builder.baseItem(Material.getMaterial(baseMaterial));
        
        builder.customModelData(section.getInt("custom_model_data", 0));
        builder.damageMultiplier(section.getDouble("damage_multiplier", 1.0));
        builder.attackSpeed(section.getDouble("attack_speed", 1.6));
        builder.rarity(WeaponRarity.fromString(section.getString("rarity", "COMMON")));
        builder.unbreakable(section.getBoolean("unbreakable", false));

        // Soulbound
        if (section.contains("soulbound")) {
            ConfigurationSection soulbound = section.getConfigurationSection("soulbound");
            if (soulbound != null) {
                builder.soulboundEnabled(soulbound.getBoolean("enabled", false));
                builder.soulboundType(soulbound.getString("type", "PERMANENT"));
                builder.otherPlayerDamagePenalty(soulbound.getInt("other_player_damage_penalty", 0));
            }
        }

        // Vanilla enchantments
        if (section.contains("enchantments.vanilla")) {
            ConfigurationSection enchants = section.getConfigurationSection("enchantments.vanilla");
            if (enchants != null) {
                Map<Enchantment, Integer> vanillaEnchants = new HashMap<>();
                for (String enchantName : enchants.getKeys(false)) {
                    try {
                        Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                        if (enchant != null) {
                            vanillaEnchants.put(enchant, enchants.getInt(enchantName));
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Invalid enchantment: " + enchantName);
                    }
                }
                builder.vanillaEnchants(vanillaEnchants);
            }
        }

        // Custom enchantments (stored as Map for flexibility)
        if (section.contains("enchantments.custom")) {
            ConfigurationSection customEnchants = section.getConfigurationSection("enchantments.custom");
            if (customEnchants != null) {
                Map<String, Object> customEnchantsMap = new HashMap<>();
                for (String key : customEnchants.getKeys(false)) {
                    customEnchantsMap.put(key, customEnchants.get(key));
                }
                builder.customEnchants(customEnchantsMap);
            }
        }

        // Abilities
        if (section.contains("abilities.active")) {
            builder.activeAbility(section.getConfigurationSection("abilities.active").getValues(true));
        }
        if (section.contains("abilities.passive")) {
            builder.passiveAbility(section.getConfigurationSection("abilities.passive").getValues(true));
        }

        // On-hit effects
        if (section.contains("on_hit_effects")) {
            List<Map<String, Object>> effects = new ArrayList<>();
            List<?> effectsList = section.getList("on_hit_effects");
            if (effectsList != null) {
                for (Object obj : effectsList) {
                    if (obj instanceof Map) {
                        effects.add((Map<String, Object>) obj);
                    }
                }
            }
            builder.onHitEffects(effects);
        }

        return builder.build();
    }

    /**
     * Get weapon by ID
     */
    public CustomWeapon getWeapon(String id) {
        return weapons.get(id);
    }

    /**
     * Get weapon dari ItemStack
     */
    public CustomWeapon getWeaponFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        if (!meta.getPersistentDataContainer().has(weaponKey, PersistentDataType.STRING)) {
            return null;
        }

        String weaponId = meta.getPersistentDataContainer().get(weaponKey, PersistentDataType.STRING);
        return getWeapon(weaponId);
    }

    /**
     * Check apakah item adalah custom weapon
     */
    public boolean isCustomWeapon(ItemStack item) {
        return getWeaponFromItem(item) != null;
    }

    /**
     * Get owner UUID dari soulbound weapon
     */
    public UUID getWeaponOwner(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        NamespacedKey ownerKey = new NamespacedKey(plugin, "weapon_owner");
        if (!meta.getPersistentDataContainer().has(ownerKey, PersistentDataType.STRING)) {
            return null;
        }

        try {
            String uuidString = meta.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get all registered weapons
     */
    public Collection<CustomWeapon> getAllWeapons() {
        return weapons.values();
    }

    /**
     * Get weapons by rarity
     */
    public List<CustomWeapon> getWeaponsByRarity(WeaponRarity rarity) {
        List<CustomWeapon> result = new ArrayList<>();
        for (CustomWeapon weapon : weapons.values()) {
            if (weapon.getRarity() == rarity) {
                result.add(weapon);
            }
        }
        return result;
    }

    /**
     * Reload weapons dari config
     */
    public void reload() {
        loadWeapons();
    }

    /**
     * Get loaded weapon count
     */
    public int getLoadedWeaponCount() {
        return weapons.size();
    }

    /**
     * Get NamespacedKey untuk weapon identification
     */
    public NamespacedKey getWeaponKey() {
        return weaponKey;
    }
}
