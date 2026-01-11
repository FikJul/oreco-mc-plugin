package com.fikjul.orecurrency;

import com.fikjul.orecurrency.commands.BalanceCommand;
import com.fikjul.orecurrency.commands.CurrencyCommand;
import com.fikjul.orecurrency.commands.ShopCommand;
import com.fikjul.orecurrency.currency.CurrencyManager;
import com.fikjul.orecurrency.shop.ShopGUI;
import com.fikjul.orecurrency.shop.ShopManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class untuk Oreco Currency System
 * Plugin ini mengimplementasikan sistem mata uang berbasis ore/ingot
 * dengan shop system untuk Minecraft Paper 1.21
 */
public class OrecoPlugin extends JavaPlugin {
    
    private CurrencyManager currencyManager;
    private ShopManager shopManager;
    private ShopGUI shopGUI;

    @Override
    public void onEnable() {
        // Save default config files
        saveDefaultConfig();
        saveResource("shops.yml", false);

        // Initialize managers
        currencyManager = new CurrencyManager(this);
        shopManager = new ShopManager(this);
        shopGUI = new ShopGUI(this);

        // Register commands
        registerCommands();

        // Register event listeners
        getServer().getPluginManager().registerEvents(shopGUI, this);

        // Log startup message
        getLogger().info("OrecoCurrency plugin has been enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("OrecoCurrency plugin has been disabled!");
    }

    /**
     * Register semua commands
     */
    private void registerCommands() {
        // Balance command
        BalanceCommand balanceCommand = new BalanceCommand(this);
        getCommand("balance").setExecutor(balanceCommand);

        // Currency command
        CurrencyCommand currencyCommand = new CurrencyCommand(this);
        getCommand("currency").setExecutor(currencyCommand);

        // Shop command
        ShopCommand shopCommand = new ShopCommand(this);
        getCommand("shop").setExecutor(shopCommand);

        getLogger().info("All commands registered successfully!");
    }

    /**
     * Getters untuk managers
     */
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }
}
