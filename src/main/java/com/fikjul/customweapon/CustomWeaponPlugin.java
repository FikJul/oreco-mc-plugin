package com.fikjul.customweapon;

import com.fikjul.customweapon.collection.CollectionManager;
import com.fikjul.customweapon.commands.GachaCommand;
import com.fikjul.customweapon.gacha.GachaManager;
import com.fikjul.customweapon.gacha.PitySystem;
import com.fikjul.customweapon.gacha.RateUpManager;
import com.fikjul.customweapon.integration.OrecoCurrencyAPI;
import com.fikjul.customweapon.listeners.BossDeathListener;
import com.fikjul.customweapon.listeners.TokenPickupListener;
import com.fikjul.customweapon.listeners.WeaponListener;
import com.fikjul.customweapon.token.TokenManager;
import com.fikjul.customweapon.utils.ConfigManager;
import com.fikjul.customweapon.weapon.WeaponManager;
import com.fikjul.customweapon.weapon.WeaponRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class untuk Custom Weapon Gacha System
 * Plugin ini mengimplementasikan sistem gacha untuk custom weapons
 * dengan abilities, enchantments, dan resource pack support.
 * 
 * HARD DEPENDENCY: OrecoCurrency plugin
 */
public class CustomWeaponPlugin extends JavaPlugin {
    
    // Managers
    private ConfigManager configManager;
    private WeaponRegistry weaponRegistry;
    private WeaponManager weaponManager;
    private TokenManager tokenManager;
    private GachaManager gachaManager;
    private PitySystem pitySystem;
    private RateUpManager rateUpManager;
    private CollectionManager collectionManager;
    private OrecoCurrencyAPI orecoCurrencyAPI;

    @Override
    public void onEnable() {
        // Check for OrecoCurrency dependency
        if (!checkDependency()) {
            getLogger().severe("════════════════════════════════════════════");
            getLogger().severe("  OrecoCurrency plugin tidak ditemukan!");
            getLogger().severe("  CustomWeaponGacha MEMBUTUHKAN OrecoCurrency!");
            getLogger().severe("  Plugin akan di-disable.");
            getLogger().severe("════════════════════════════════════════════");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize config files
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.saveResourceIfNotExists("weapons.yml");
        configManager.saveResourceIfNotExists("banners.yml");
        configManager.saveResourceIfNotExists("drops.yml");

        // Initialize OrecoCurrency API
        orecoCurrencyAPI = new OrecoCurrencyAPI(this);

        // Initialize weapon system
        weaponRegistry = new WeaponRegistry(this);
        weaponManager = new WeaponManager(this);
        weaponRegistry.loadWeapons();

        // Initialize token system
        tokenManager = new TokenManager(this);

        // Initialize gacha system
        pitySystem = new PitySystem(this);
        rateUpManager = new RateUpManager(this);
        gachaManager = new GachaManager(this);

        // Initialize collection system
        collectionManager = new CollectionManager(this);

        // Register commands
        registerCommands();

        // Register event listeners
        registerListeners();

        // Log startup message
        getLogger().info("════════════════════════════════════════════");
        getLogger().info("  CustomWeaponGacha v" + getDescription().getVersion());
        getLogger().info("  Plugin berhasil dimuat!");
        getLogger().info("  OrecoCurrency integration: ✓ Aktif");
        getLogger().info("  Loaded weapons: " + weaponRegistry.getLoadedWeaponCount());
        getLogger().info("════════════════════════════════════════════");
    }

    @Override
    public void onDisable() {
        // Save data
        if (tokenManager != null) {
            tokenManager.saveAll();
        }
        if (pitySystem != null) {
            pitySystem.saveAll();
        }
        if (collectionManager != null) {
            collectionManager.saveAll();
        }

        getLogger().info("CustomWeaponGacha plugin has been disabled!");
    }

    /**
     * Check apakah OrecoCurrency plugin tersedia
     */
    private boolean checkDependency() {
        Plugin orecoCurrency = getServer().getPluginManager().getPlugin("OrecoCurrency");
        return orecoCurrency != null && orecoCurrency.isEnabled();
    }

    /**
     * Register semua commands
     */
    private void registerCommands() {
        GachaCommand gachaCommand = new GachaCommand(this);
        getCommand("gacha").setExecutor(gachaCommand);
        getCommand("gacha").setTabCompleter(gachaCommand);
        
        getLogger().info("Commands registered successfully!");
    }

    /**
     * Register semua event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getServer().getPluginManager().registerEvents(new BossDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new TokenPickupListener(this), this);
        
        getLogger().info("Event listeners registered successfully!");
    }

    // Getters untuk managers
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WeaponRegistry getWeaponRegistry() {
        return weaponRegistry;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public GachaManager getGachaManager() {
        return gachaManager;
    }

    public PitySystem getPitySystem() {
        return pitySystem;
    }

    public RateUpManager getRateUpManager() {
        return rateUpManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public OrecoCurrencyAPI getOrecoCurrencyAPI() {
        return orecoCurrencyAPI;
    }
}
