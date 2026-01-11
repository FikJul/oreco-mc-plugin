package com.fikjul.customweapon.weapon;

import com.fikjul.customweapon.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class yang merepresentasikan custom weapon
 */
public class CustomWeapon {
    
    private final String id;
    private final String displayName;
    private final List<String> lore;
    private final Material baseItem;
    private final int customModelData;
    private final double damageMultiplier;
    private final double attackSpeed;
    private final WeaponRarity rarity;
    private final boolean unbreakable;
    
    // Soulbound configuration
    private final boolean soulboundEnabled;
    private final String soulboundType;
    private final int otherPlayerDamagePenalty;
    
    // Enchantments
    private final Map<Enchantment, Integer> vanillaEnchants;
    private final Map<String, Object> customEnchants;
    
    // Abilities & Effects
    private final Map<String, Object> activeAbility;
    private final Map<String, Object> passiveAbility;
    private final List<Map<String, Object>> onHitEffects;

    private CustomWeapon(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.baseItem = builder.baseItem;
        this.customModelData = builder.customModelData;
        this.damageMultiplier = builder.damageMultiplier;
        this.attackSpeed = builder.attackSpeed;
        this.rarity = builder.rarity;
        this.unbreakable = builder.unbreakable;
        this.soulboundEnabled = builder.soulboundEnabled;
        this.soulboundType = builder.soulboundType;
        this.otherPlayerDamagePenalty = builder.otherPlayerDamagePenalty;
        this.vanillaEnchants = builder.vanillaEnchants;
        this.customEnchants = builder.customEnchants;
        this.activeAbility = builder.activeAbility;
        this.passiveAbility = builder.passiveAbility;
        this.onHitEffects = builder.onHitEffects;
    }

    /**
     * Create ItemStack dari weapon ini
     */
    public ItemStack createItemStack(NamespacedKey weaponKey) {
        return createItemStack(weaponKey, null);
    }

    /**
     * Create ItemStack dengan owner (untuk soulbound)
     */
    public ItemStack createItemStack(NamespacedKey weaponKey, UUID ownerUUID) {
        ItemStack item = new ItemStack(baseItem);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name
            meta.setDisplayName(MessageUtil.color(displayName));

            // Set lore
            List<String> finalLore = new ArrayList<>();
            for (String line : lore) {
                // Replace {player} dengan owner name jika soulbound
                if (ownerUUID != null && line.contains("{player}")) {
                    String ownerName = org.bukkit.Bukkit.getOfflinePlayer(ownerUUID).getName();
                    finalLore.add(MessageUtil.color(line.replace("{player}", ownerName != null ? ownerName : "Unknown")));
                } else {
                    finalLore.add(MessageUtil.color(line));
                }
            }
            meta.setLore(finalLore);

            // Set custom model data
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }

            // Set unbreakable
            if (unbreakable) {
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }

            // Add vanilla enchantments
            for (Map.Entry<Enchantment, Integer> entry : vanillaEnchants.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }

            // Hide enchantments if needed
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            // Set NBT data untuk identifikasi weapon
            meta.getPersistentDataContainer().set(weaponKey, PersistentDataType.STRING, id);
            
            // Set owner UUID jika soulbound
            if (soulboundEnabled && ownerUUID != null) {
                NamespacedKey ownerKey = new NamespacedKey(weaponKey.getNamespace(), "weapon_owner");
                meta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, ownerUUID.toString());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public Material getBaseItem() { return baseItem; }
    public int getCustomModelData() { return customModelData; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public double getAttackSpeed() { return attackSpeed; }
    public WeaponRarity getRarity() { return rarity; }
    public boolean isUnbreakable() { return unbreakable; }
    public boolean isSoulboundEnabled() { return soulboundEnabled; }
    public int getOtherPlayerDamagePenalty() { return otherPlayerDamagePenalty; }
    public Map<Enchantment, Integer> getVanillaEnchants() { return vanillaEnchants; }
    public Map<String, Object> getActiveAbility() { return activeAbility; }
    public Map<String, Object> getPassiveAbility() { return passiveAbility; }
    public List<Map<String, Object>> getOnHitEffects() { return onHitEffects; }

    /**
     * Builder pattern untuk construct CustomWeapon
     */
    public static class Builder {
        private String id;
        private String displayName;
        private List<String> lore = new ArrayList<>();
        private Material baseItem;
        private int customModelData;
        private double damageMultiplier = 1.0;
        private double attackSpeed = 1.6;
        private WeaponRarity rarity = WeaponRarity.COMMON;
        private boolean unbreakable = false;
        private boolean soulboundEnabled = false;
        private String soulboundType = "PERMANENT";
        private int otherPlayerDamagePenalty = 0;
        private Map<Enchantment, Integer> vanillaEnchants = new HashMap<>();
        private Map<String, Object> customEnchants = new HashMap<>();
        private Map<String, Object> activeAbility = new HashMap<>();
        private Map<String, Object> passiveAbility = new HashMap<>();
        private List<Map<String, Object>> onHitEffects = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder baseItem(Material baseItem) {
            this.baseItem = baseItem;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder damageMultiplier(double damageMultiplier) {
            this.damageMultiplier = damageMultiplier;
            return this;
        }

        public Builder attackSpeed(double attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        public Builder rarity(WeaponRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public Builder soulboundEnabled(boolean enabled) {
            this.soulboundEnabled = enabled;
            return this;
        }

        public Builder soulboundType(String type) {
            this.soulboundType = type;
            return this;
        }

        public Builder otherPlayerDamagePenalty(int penalty) {
            this.otherPlayerDamagePenalty = penalty;
            return this;
        }

        public Builder vanillaEnchants(Map<Enchantment, Integer> enchants) {
            this.vanillaEnchants = enchants;
            return this;
        }

        public Builder customEnchants(Map<String, Object> enchants) {
            this.customEnchants = enchants;
            return this;
        }

        public Builder activeAbility(Map<String, Object> ability) {
            this.activeAbility = ability;
            return this;
        }

        public Builder passiveAbility(Map<String, Object> ability) {
            this.passiveAbility = ability;
            return this;
        }

        public Builder onHitEffects(List<Map<String, Object>> effects) {
            this.onHitEffects = effects;
            return this;
        }

        public CustomWeapon build() {
            return new CustomWeapon(this);
        }
    }
}
