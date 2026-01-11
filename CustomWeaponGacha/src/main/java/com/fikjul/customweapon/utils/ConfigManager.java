package com.fikjul.customweapon.utils;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Manager untuk mengelola multiple config files
 */
public class ConfigManager {
    private final CustomWeaponPlugin plugin;
    private FileConfiguration weaponsConfig;
    private FileConfiguration bannersConfig;
    private FileConfiguration dropsConfig;
    private FileConfiguration gachaConfig;

    public ConfigManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    /**
     * Load semua config files
     */
    private void loadConfigs() {
        weaponsConfig = loadConfig("weapons.yml");
        bannersConfig = loadConfig("banners.yml");
        dropsConfig = loadConfig("drops.yml");
        gachaConfig = loadConfig("config.yml");
    }

    /**
     * Load config file dari resources atau data folder
     */
    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            saveResourceIfNotExists(fileName);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Save resource dari JAR ke data folder jika belum ada
     */
    public void saveResourceIfNotExists(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            try {
                // Create parent directories
                file.getParentFile().mkdirs();
                
                // Try with customweapon/ prefix first
                String fullPath = "customweapon/" + resourcePath;
                InputStream inputStream = plugin.getResource(fullPath);
                
                // Fallback to direct path if not found
                if (inputStream == null) {
                    inputStream = plugin.getResource(resourcePath);
                }
                
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                    inputStream.close();
                } else {
                    plugin.getLogger().warning("Resource tidak ditemukan: " + resourcePath);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Gagal menyimpan resource: " + resourcePath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Reload semua config files
     */
    public void reloadAll() {
        plugin.reloadConfig();
        loadConfigs();
        plugin.getLogger().info("Semua konfigurasi berhasil di-reload!");
    }

    /**
     * Save config file
     */
    public void saveConfig(String fileName, FileConfiguration config) {
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Gagal menyimpan config: " + fileName);
            e.printStackTrace();
        }
    }

    // Getters
    public FileConfiguration getWeaponsConfig() {
        return weaponsConfig;
    }

    public FileConfiguration getBannersConfig() {
        return bannersConfig;
    }

    public FileConfiguration getDropsConfig() {
        return dropsConfig;
    }

    public FileConfiguration getGachaConfig() {
        return gachaConfig;
    }
}
