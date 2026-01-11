package com.fikjul.customweapon.integration;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyManager;
import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * API untuk integrasi dengan OrecoCurrency plugin
 * Menyediakan method untuk deduct currency dari player
 */
public class OrecoCurrencyAPI {
    
    private final CustomWeaponPlugin plugin;
    private OrecoPlugin orecoCurrencyPlugin;
    private CurrencyManager currencyManager;

    public OrecoCurrencyAPI(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        loadOrecoCurrency();
    }

    /**
     * Load OrecoCurrency plugin instance
     */
    private void loadOrecoCurrency() {
        Plugin orecoCurrency = plugin.getServer().getPluginManager().getPlugin("OrecoCurrency");
        
        if (orecoCurrency instanceof OrecoPlugin) {
            this.orecoCurrencyPlugin = (OrecoPlugin) orecoCurrency;
            this.currencyManager = orecoCurrencyPlugin.getCurrencyManager();
            plugin.getLogger().info("OrecoCurrency integration berhasil dimuat!");
        } else {
            plugin.getLogger().severe("OrecoCurrency plugin tidak valid!");
        }
    }

    /**
     * Check apakah integration aktif
     */
    public boolean isEnabled() {
        return orecoCurrencyPlugin != null && currencyManager != null;
    }

    /**
     * Deduct diamond dari enderchest player untuk beli token
     * 
     * @param player Player yang akan di-deduct
     * @param amount Jumlah diamond yang dibutuhkan
     * @return true jika berhasil
     */
    public boolean deductDiamonds(Player player, int amount) {
        if (!isEnabled()) {
            plugin.getLogger().warning("OrecoCurrency integration tidak aktif!");
            return false;
        }

        // Buat price map untuk diamond
        Map<CurrencyType, Integer> prices = new HashMap<>();
        prices.put(CurrencyType.DIAMOND, amount);

        // Deduct menggunakan CurrencyManager dari OrecoCurrency
        return currencyManager.deductCurrency(player, prices);
    }

    /**
     * Check apakah player punya cukup diamond
     */
    public boolean hasDiamonds(Player player, int amount) {
        if (!isEnabled()) {
            return false;
        }

        Map<CurrencyType, Integer> balance = currencyManager.getBalance(player);
        return balance.get(CurrencyType.DIAMOND) >= amount;
    }

    /**
     * Get balance player (semua currency)
     */
    public Map<CurrencyType, Integer> getBalance(Player player) {
        if (!isEnabled()) {
            return new HashMap<>();
        }

        return currencyManager.getBalance(player);
    }

    /**
     * Get specific currency balance
     */
    public int getCurrencyBalance(Player player, CurrencyType type) {
        if (!isEnabled()) {
            return 0;
        }

        Map<CurrencyType, Integer> balance = currencyManager.getBalance(player);
        return balance.getOrDefault(type, 0);
    }

    /**
     * Format balance untuk display
     */
    public String formatBalance(Player player) {
        if (!isEnabled()) {
            return "N/A";
        }

        Map<CurrencyType, Integer> balance = currencyManager.getBalance(player);
        return currencyManager.formatBalance(balance);
    }

    /**
     * Get CurrencyManager instance (untuk advanced usage)
     */
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    /**
     * Get OrecoPlugin instance
     */
    public OrecoPlugin getOrecoCurrencyPlugin() {
        return orecoCurrencyPlugin;
    }
}
