package com.fikjul.customweapon.token;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Manager untuk mengelola gacha tokens
 */
public class TokenManager {
    
    private final CustomWeaponPlugin plugin;
    private final TokenStorage storage;
    private final TokenItem tokenItem;

    public TokenManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.storage = new TokenStorage(plugin);
        this.tokenItem = new TokenItem(plugin);
    }

    /**
     * Get token balance player
     */
    public int getBalance(Player player) {
        return storage.getBalance(player.getUniqueId());
    }

    /**
     * Get token balance by UUID
     */
    public int getBalance(UUID uuid) {
        return storage.getBalance(uuid);
    }

    /**
     * Add token ke player balance
     */
    public void addTokens(Player player, int amount) {
        storage.addBalance(player.getUniqueId(), amount);
    }

    /**
     * Remove token dari player balance
     * @return true jika berhasil
     */
    public boolean removeTokens(Player player, int amount) {
        return storage.removeBalance(player.getUniqueId(), amount);
    }

    /**
     * Check apakah player punya cukup token
     */
    public boolean hasEnoughTokens(Player player, int amount) {
        return storage.hasEnough(player.getUniqueId(), amount);
    }

    /**
     * Purchase tokens dengan diamonds (via OrecoCurrency)
     */
    public boolean purchaseTokens(Player player, int tokenAmount) {
        // Get cost dari config
        int costPerToken = plugin.getConfigManager().getGachaConfig()
                .getInt("token_purchase.cost_per_token", 64);
        int totalCost = costPerToken * tokenAmount;

        // Deduct diamonds via OrecoCurrency
        if (!plugin.getOrecoCurrencyAPI().deductDiamonds(player, totalCost)) {
            String message = plugin.getConfigManager().getGachaConfig()
                    .getString("messages.token_insufficient_diamond", 
                            "&cDiamond tidak cukup! Butuh: {cost} Diamond");
            MessageUtil.send(player, message, "{cost}", String.valueOf(totalCost));
            return false;
        }

        // Add tokens
        addTokens(player, tokenAmount);

        // Send success message
        String message = plugin.getConfigManager().getGachaConfig()
                .getString("messages.token_purchase_success",
                        "&aBerhasil membeli {amount} Token dengan {cost} Diamond!");
        MessageUtil.send(player, message,
                "{amount}", String.valueOf(tokenAmount),
                "{cost}", String.valueOf(totalCost));

        return true;
    }

    /**
     * Redeem physical token item (dari inventory ke balance)
     */
    public boolean redeemTokenItem(Player player, ItemStack item) {
        if (!tokenItem.isToken(item)) {
            return false;
        }

        int amount = tokenItem.getTokenAmount(item);
        
        // Add ke balance
        addTokens(player, amount);
        
        // Remove dari inventory
        item.setAmount(0);

        // Send message
        String message = plugin.getConfigManager().getGachaConfig()
                .getString("messages.token_added",
                        "&aToken berhasil ditambahkan! Total: {total} Token");
        MessageUtil.send(player, message,
                "{total}", String.valueOf(getBalance(player)));

        return true;
    }

    /**
     * Create physical token item
     */
    public ItemStack createTokenItem(int amount) {
        return tokenItem.createToken(amount);
    }

    /**
     * Check if item is a token
     */
    public boolean isTokenItem(ItemStack item) {
        return tokenItem.isToken(item);
    }

    /**
     * Give physical token ke player
     */
    public void givePhysicalToken(Player player, int amount) {
        ItemStack token = tokenItem.createToken(amount);
        
        // Try add to inventory
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(token);
        } else {
            // Drop di ground jika inventory full
            player.getWorld().dropItemNaturally(player.getLocation(), token);
        }
    }

    /**
     * Save all data
     */
    public void saveAll() {
        storage.save();
    }

    /**
     * Get total tokens dalam ekonomi
     */
    public long getTotalTokens() {
        return storage.getTotalTokens();
    }

    /**
     * Get jumlah players yang punya token
     */
    public int getPlayersWithTokens() {
        return storage.getPlayersWithTokens();
    }

    /**
     * Get TokenStorage instance
     */
    public TokenStorage getStorage() {
        return storage;
    }

    /**
     * Get TokenItem instance
     */
    public TokenItem getTokenItem() {
        return tokenItem;
    }
}
