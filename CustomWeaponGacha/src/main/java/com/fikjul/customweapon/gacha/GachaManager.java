package com.fikjul.customweapon.gacha;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.utils.MessageUtil;
import com.fikjul.customweapon.weapon.WeaponRarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Manager untuk gacha system
 */
public class GachaManager {
    
    private final CustomWeaponPlugin plugin;
    private final Map<String, GachaBanner> banners;
    private final Random random;

    public GachaManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.banners = new HashMap<>();
        this.random = new Random();
        loadBanners();
    }

    /**
     * Load banners dari config
     */
    public void loadBanners() {
        banners.clear();
        
        FileConfiguration config = plugin.getConfigManager().getBannersConfig();
        ConfigurationSection bannersSection = config.getConfigurationSection("banners");
        
        if (bannersSection == null) {
            plugin.getLogger().warning("No banners found in banners.yml!");
            return;
        }

        for (String bannerId : bannersSection.getKeys(false)) {
            try {
                ConfigurationSection bannerSection = bannersSection.getConfigurationSection(bannerId);
                GachaBanner banner = new GachaBanner(bannerId, bannerSection);
                banners.put(bannerId, banner);
                
                if (banner.isEnabled()) {
                    plugin.getLogger().info("Loaded banner: " + bannerId + " (" + banner.getDisplayName() + ")");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load banner: " + bannerId);
                e.printStackTrace();
            }
        }
    }

    /**
     * Perform single roll
     */
    public GachaResult rollSingle(Player player, String bannerId) {
        GachaBanner banner = banners.get(bannerId);
        if (banner == null || !banner.isEnabled()) {
            return null;
        }

        // Check token balance
        if (!plugin.getTokenManager().hasEnoughTokens(player, banner.getCostSingle())) {
            String message = plugin.getConfigManager().getGachaConfig()
                    .getString("messages.gacha_insufficient_token",
                            "&cToken tidak cukup! Butuh: {cost} Token");
            MessageUtil.send(player, message, "{cost}", String.valueOf(banner.getCostSingle()));
            return null;
        }

        // Deduct tokens
        plugin.getTokenManager().removeTokens(player, banner.getCostSingle());

        // Perform roll
        WeaponRarity rarity = determineRarity(player, banner);
        GachaBanner.DropItem dropItem = banner.getRandomDrop(rarity, random);

        // Handle pity
        plugin.getPitySystem().handlePityAfterRoll(player, bannerId, rarity.name());

        // Create result
        GachaResult result = new GachaResult();
        result.rarity = rarity;
        result.dropItem = dropItem;
        result.rewardItem = createRewardItem(player, dropItem);

        return result;
    }

    /**
     * Perform 10x roll
     */
    public List<GachaResult> rollMulti(Player player, String bannerId) {
        GachaBanner banner = banners.get(bannerId);
        if (banner == null || !banner.isEnabled()) {
            return null;
        }

        // Check token balance
        if (!plugin.getTokenManager().hasEnoughTokens(player, banner.getCostMulti())) {
            String message = plugin.getConfigManager().getGachaConfig()
                    .getString("messages.gacha_insufficient_token",
                            "&cToken tidak cukup! Butuh: {cost} Token");
            MessageUtil.send(player, message, "{cost}", String.valueOf(banner.getCostMulti()));
            return null;
        }

        // Deduct tokens
        plugin.getTokenManager().removeTokens(player, banner.getCostMulti());

        // Perform 10 rolls
        List<GachaResult> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            WeaponRarity rarity = determineRarity(player, banner);
            GachaBanner.DropItem dropItem = banner.getRandomDrop(rarity, random);
            
            // Handle pity
            plugin.getPitySystem().handlePityAfterRoll(player, bannerId, rarity.name());

            GachaResult result = new GachaResult();
            result.rarity = rarity;
            result.dropItem = dropItem;
            result.rewardItem = createRewardItem(player, dropItem);
            
            results.add(result);
        }

        return results;
    }

    /**
     * Determine rarity untuk roll dengan pity system
     */
    private WeaponRarity determineRarity(Player player, GachaBanner banner) {
        PitySystem pity = plugin.getPitySystem();
        
        // Hard pity untuk GOD tier
        if (pity.isHardPityGod(player, banner.getId())) {
            return WeaponRarity.GOD;
        }

        // Hard pity untuk MYTHIC+
        if (pity.isHardPityMythic(player, banner.getId())) {
            // 50/50 between MYTHIC and GOD
            return random.nextBoolean() ? WeaponRarity.GOD : WeaponRarity.MYTHIC;
        }

        // Calculate rates dengan soft pity dan rate-up
        Map<WeaponRarity, Double> rates = new HashMap<>(banner.getDropRates());
        
        // Apply soft pity bonus ke GOD tier
        if (pity.isInSoftPity(player, banner.getId())) {
            double bonus = pity.getSoftPityBonus(player, banner.getId());
            double currentGodRate = rates.getOrDefault(WeaponRarity.GOD, 1.0);
            rates.put(WeaponRarity.GOD, currentGodRate + bonus);
        }

        // Apply rate-up multipliers
        RateUpManager rateUpManager = plugin.getRateUpManager();
        for (WeaponRarity rarity : rates.keySet()) {
            double multiplier = rateUpManager.getRateMultiplier(banner.getId(), rarity.name());
            if (multiplier > 1.0) {
                rates.put(rarity, rates.get(rarity) * multiplier);
            }
        }

        // Normalize rates to 100%
        double total = rates.values().stream().mapToDouble(Double::doubleValue).sum();
        for (WeaponRarity rarity : rates.keySet()) {
            rates.put(rarity, (rates.get(rarity) / total) * 100.0);
        }

        // Weighted random selection
        double roll = random.nextDouble() * 100.0;
        double cumulative = 0.0;

        // Order by tier (GOD first, then MYTHIC, etc.)
        List<WeaponRarity> sortedRarities = new ArrayList<>(rates.keySet());
        sortedRarities.sort((a, b) -> Integer.compare(b.getTier(), a.getTier()));

        for (WeaponRarity rarity : sortedRarities) {
            cumulative += rates.get(rarity);
            if (roll < cumulative) {
                return rarity;
            }
        }

        return WeaponRarity.COMMON; // Fallback
    }

    /**
     * Create reward ItemStack dari DropItem
     */
    private ItemStack createRewardItem(Player player, GachaBanner.DropItem dropItem) {
        if (dropItem == null) {
            return new ItemStack(org.bukkit.Material.STONE);
        }

        if (dropItem.type == GachaBanner.DropItemType.WEAPON) {
            // Create custom weapon
            return plugin.getWeaponManager().createWeaponItem(dropItem.weaponId, player.getUniqueId());
        } else {
            // Create material item
            ItemStack item = new ItemStack(dropItem.material, dropItem.amount);
            
            // Apply custom name dan enchantments
            if (dropItem.customName != null || dropItem.enchantments != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (dropItem.customName != null) {
                        meta.setDisplayName(MessageUtil.color(dropItem.customName));
                    }
                    if (dropItem.enchantments != null) {
                        for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : dropItem.enchantments.entrySet()) {
                            meta.addEnchant(entry.getKey(), entry.getValue(), true);
                        }
                    }
                    item.setItemMeta(meta);
                }
            }
            
            return item;
        }
    }

    /**
     * Get banner by ID
     */
    public GachaBanner getBanner(String id) {
        return banners.get(id);
    }

    /**
     * Get all enabled banners
     */
    public List<GachaBanner> getEnabledBanners() {
        List<GachaBanner> enabled = new ArrayList<>();
        for (GachaBanner banner : banners.values()) {
            if (banner.isEnabled()) {
                enabled.add(banner);
            }
        }
        return enabled;
    }

    /**
     * Reload banners
     */
    public void reload() {
        loadBanners();
    }

    /**
     * Gacha result
     */
    public static class GachaResult {
        public WeaponRarity rarity;
        public GachaBanner.DropItem dropItem;
        public ItemStack rewardItem;
    }
}
