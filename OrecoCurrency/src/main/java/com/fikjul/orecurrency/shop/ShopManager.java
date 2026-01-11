package com.fikjul.orecurrency.shop;

import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manager untuk mengelola data shop dan transaksi
 */
public class ShopManager {
    private final OrecoPlugin plugin;
    private final Map<Material, ShopItem> shopItems;
    private final Map<String, List<ShopItem>> categorizedItems;
    private FileConfiguration shopsConfig;
    private File shopsFile;

    public ShopManager(OrecoPlugin plugin) {
        this.plugin = plugin;
        this.shopItems = new HashMap<>();
        this.categorizedItems = new HashMap<>();
        loadShopsConfig();
    }

    /**
     * Load konfigurasi shops.yml
     */
    public void loadShopsConfig() {
        shopsFile = new File(plugin.getDataFolder(), "shops.yml");
        
        if (!shopsFile.exists()) {
            plugin.saveResource("shops.yml", false);
        }

        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);
        loadItems();
    }

    /**
     * Reload konfigurasi shops
     */
    public void reloadShops() {
        shopItems.clear();
        categorizedItems.clear();
        loadShopsConfig();
    }

    /**
     * Load semua items dari config
     */
    private void loadItems() {
        ConfigurationSection itemsSection = shopsConfig.getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            try {
                Material material = Material.valueOf(key);
                ShopItem item = new ShopItem(material);

                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                
                // Load display name
                if (itemSection.contains("display_name")) {
                    item.setDisplayName(itemSection.getString("display_name"));
                }

                // Load category
                if (itemSection.contains("category")) {
                    item.setCategory(itemSection.getString("category"));
                }

                // Load price
                ConfigurationSection priceSection = itemSection.getConfigurationSection("price");
                if (priceSection != null) {
                    Map<CurrencyType, Integer> prices = new HashMap<>();
                    for (String currencyName : priceSection.getKeys(false)) {
                        CurrencyType type = CurrencyType.fromString(currencyName);
                        if (type != null) {
                            prices.put(type, priceSection.getInt(currencyName));
                        }
                    }
                    item.setPrice(prices);
                }

                // Load lore
                if (itemSection.contains("lore")) {
                    item.setLore(itemSection.getStringList("lore"));
                }

                shopItems.put(material, item);

                // Kategorisasi
                String category = item.getCategory();
                categorizedItems.computeIfAbsent(category, k -> new ArrayList<>()).add(item);

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material in shops.yml: " + key);
            }
        }

        plugin.getLogger().info("Loaded " + shopItems.size() + " shop items");
    }

    /**
     * Mendapatkan item dari shop
     */
    public ShopItem getItem(Material material) {
        return shopItems.get(material);
    }

    /**
     * Mendapatkan semua items di shop
     */
    public Collection<ShopItem> getAllItems() {
        return shopItems.values();
    }

    /**
     * Mendapatkan items berdasarkan kategori
     */
    public List<ShopItem> getItemsByCategory(String category) {
        return categorizedItems.getOrDefault(category, new ArrayList<>());
    }

    /**
     * Mendapatkan semua kategori yang ada
     */
    public Set<String> getCategories() {
        return new HashSet<>(categorizedItems.keySet());
    }

    /**
     * Tambah item ke shop (admin command)
     */
    public boolean addItem(Material material, Map<CurrencyType, Integer> prices, String category) {
        ShopItem item = new ShopItem(material);
        item.setPrice(prices);
        
        // Tentukan kategori otomatis jika tidak dispesifikkan
        if (category == null) {
            category = determineCategory(material);
        }
        item.setCategory(category);

        // Simpan ke memory
        shopItems.put(material, item);
        categorizedItems.computeIfAbsent(category, k -> new ArrayList<>()).add(item);

        // Simpan ke config
        saveItemToConfig(item);

        return true;
    }

    /**
     * Hapus item dari shop
     */
    public boolean removeItem(Material material) {
        ShopItem item = shopItems.remove(material);
        if (item == null) return false;

        // Hapus dari kategori
        List<ShopItem> categoryList = categorizedItems.get(item.getCategory());
        if (categoryList != null) {
            categoryList.remove(item);
        }

        // Hapus dari config
        shopsConfig.set("items." + material.name(), null);
        saveConfig();

        return true;
    }

    /**
     * Set kategori item
     */
    public boolean setCategory(Material material, String newCategory) {
        ShopItem item = shopItems.get(material);
        if (item == null) return false;

        String oldCategory = item.getCategory();
        
        // Hapus dari kategori lama
        List<ShopItem> oldList = categorizedItems.get(oldCategory);
        if (oldList != null) {
            oldList.remove(item);
        }

        // Tambah ke kategori baru
        item.setCategory(newCategory);
        categorizedItems.computeIfAbsent(newCategory, k -> new ArrayList<>()).add(item);

        // Update config
        shopsConfig.set("items." + material.name() + ".category", newCategory);
        saveConfig();

        return true;
    }

    /**
     * Simpan item ke config
     */
    private void saveItemToConfig(ShopItem item) {
        String path = "items." + item.getMaterial().name();
        
        shopsConfig.set(path + ".category", item.getCategory());
        
        // Simpan price
        for (Map.Entry<CurrencyType, Integer> entry : item.getPrice().entrySet()) {
            shopsConfig.set(path + ".price." + entry.getKey().name().toLowerCase(), entry.getValue());
        }

        saveConfig();
    }

    /**
     * Save config ke file
     */
    private void saveConfig() {
        try {
            shopsConfig.save(shopsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save shops.yml: " + e.getMessage());
        }
    }

    /**
     * Tentukan kategori berdasarkan material type
     */
    private String determineCategory(Material material) {
        String name = material.name().toLowerCase();

        if (name.contains("sword") || name.contains("axe") || name.contains("bow") || name.contains("trident")) {
            return "Weapons";
        } else if (name.contains("helmet") || name.contains("chestplate") || name.contains("leggings") || name.contains("boots")) {
            return "Armor";
        } else if (name.contains("pickaxe") || name.contains("shovel") || name.contains("hoe")) {
            return "Tools";
        } else if (material.isBlock()) {
            return "Blocks";
        } else if (material.isEdible()) {
            return "Food";
        } else if (name.contains("redstone") || name.contains("repeater") || name.contains("comparator") || name.contains("piston")) {
            return "Redstone";
        } else {
            return "Items";
        }
    }

    /**
     * Mendapatkan informasi kategori dari config
     */
    public Map<String, Map<String, Object>> getCategoryInfo() {
        Map<String, Map<String, Object>> categoryInfo = new HashMap<>();
        
        ConfigurationSection categoriesSection = shopsConfig.getConfigurationSection("categories");
        if (categoriesSection != null) {
            for (String categoryName : categoriesSection.getKeys(false)) {
                ConfigurationSection categorySection = categoriesSection.getConfigurationSection(categoryName);
                Map<String, Object> info = new HashMap<>();
                
                info.put("display_name", categorySection.getString("display_name", categoryName));
                info.put("icon", categorySection.getString("icon", "CHEST"));
                info.put("slot", categorySection.getInt("slot", 0));
                
                categoryInfo.put(categoryName, info);
            }
        }
        
        return categoryInfo;
    }
}
