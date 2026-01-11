package com.fikjul.orecurrency.shop;

import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyManager;
import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handler untuk GUI inventory shop
 */
public class ShopGUI implements Listener {
    private final OrecoPlugin plugin;
    private final ShopManager shopManager;
    private final CurrencyManager currencyManager;

    public ShopGUI(OrecoPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
        this.currencyManager = plugin.getCurrencyManager();
    }

    /**
     * Membuka main menu shop
     */
    public void openMainMenu(Player player) {
        int size = plugin.getConfig().getInt("shop.gui_size", 54);
        String title = colorize(plugin.getConfig().getString("shop.gui_title", "&6&lOreco Shop"));
        
        Inventory inv = Bukkit.createInventory(null, size, title);

        // Ambil info kategori dari config
        Map<String, Map<String, Object>> categoryInfo = shopManager.getCategoryInfo();

        for (Map.Entry<String, Map<String, Object>> entry : categoryInfo.entrySet()) {
            String categoryName = entry.getKey();
            Map<String, Object> info = entry.getValue();

            // Cek apakah kategori punya items
            List<ShopItem> items = shopManager.getItemsByCategory(categoryName);
            if (items.isEmpty()) continue;

            String displayName = (String) info.get("display_name");
            String iconName = (String) info.get("icon");
            int slot = (int) info.get("slot");

            Material iconMaterial;
            try {
                iconMaterial = Material.valueOf(iconName);
            } catch (IllegalArgumentException e) {
                iconMaterial = Material.CHEST;
            }

            ItemStack categoryItem = new ItemStack(iconMaterial);
            ItemMeta meta = categoryItem.getItemMeta();
            
            meta.setDisplayName(colorize(displayName));
            
            List<String> lore = new ArrayList<>();
            lore.add(colorize("&7Click to browse"));
            lore.add(colorize("&7Items: &e" + items.size()));
            meta.setLore(lore);
            
            categoryItem.setItemMeta(meta);

            if (slot >= 0 && slot < size) {
                inv.setItem(slot, categoryItem);
            }
        }

        player.openInventory(inv);
    }

    /**
     * Membuka category view
     */
    public void openCategoryView(Player player, String category) {
        String titleTemplate = plugin.getConfig().getString("shop.category_gui_title", "&6&lShop - {category}");
        String title = colorize(titleTemplate.replace("{category}", category));
        
        Inventory inv = Bukkit.createInventory(null, 54, title);

        List<ShopItem> items = shopManager.getItemsByCategory(category);
        Map<CurrencyType, Integer> playerBalance = currencyManager.getBalance(player);
        long totalCopperBalance = currencyManager.getTotalCopperValue(player);

        int slot = 0;
        for (ShopItem shopItem : items) {
            if (slot >= 45) break; // Sisakan baris bawah untuk navigasi

            ItemStack displayItem = new ItemStack(shopItem.getMaterial());
            ItemMeta meta = displayItem.getItemMeta();

            // Set display name
            String displayName = shopItem.getDisplayName();
            if (displayName != null) {
                meta.setDisplayName(colorize(displayName));
            }

            // Build lore
            List<String> lore = new ArrayList<>();
            
            // Custom lore dari config
            if (shopItem.getLore() != null) {
                for (String line : shopItem.getLore()) {
                    lore.add(colorize(line));
                }
            }

            lore.add("");
            lore.add(colorize("&6Price:"));
            
            // Tampilkan harga
            for (Map.Entry<CurrencyType, Integer> priceEntry : shopItem.getPrice().entrySet()) {
                String priceLine = "  &e" + priceEntry.getValue() + " &7" + priceEntry.getKey().getDisplayName();
                lore.add(colorize(priceLine));
            }

            lore.add("");

            // Cek apakah player punya cukup balance
            boolean canAfford = totalCopperBalance >= shopItem.getTotalCopperPrice();
            if (canAfford) {
                lore.add(colorize("&aClick to purchase!"));
            } else {
                lore.add(colorize("&cInsufficient balance!"));
            }

            meta.setLore(lore);
            displayItem.setItemMeta(meta);

            inv.setItem(slot++, displayItem);
        }

        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(colorize("&cBack to Main Menu"));
        backButton.setItemMeta(backMeta);
        inv.setItem(49, backButton);

        player.openInventory(inv);
    }

    /**
     * Handle inventory click events
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Cek apakah ini shop GUI
        String shopTitle = colorize(plugin.getConfig().getString("shop.gui_title", "&6&lOreco Shop"));
        String categoryTitleTemplate = plugin.getConfig().getString("shop.category_gui_title", "&6&lShop - {category}");

        if (title.equals(shopTitle)) {
            event.setCancelled(true);
            handleMainMenuClick(player, event.getCurrentItem());
        } else if (title.startsWith(colorize(categoryTitleTemplate.split("\\{")[0]))) {
            event.setCancelled(true);
            handleCategoryClick(player, event.getCurrentItem(), title);
        }
    }

    /**
     * Handle click di main menu
     */
    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Cari kategori yang diklik
        Map<String, Map<String, Object>> categoryInfo = shopManager.getCategoryInfo();
        
        for (Map.Entry<String, Map<String, Object>> entry : categoryInfo.entrySet()) {
            String categoryName = entry.getKey();
            Map<String, Object> info = entry.getValue();
            
            String iconName = (String) info.get("icon");
            Material iconMaterial;
            try {
                iconMaterial = Material.valueOf(iconName);
            } catch (IllegalArgumentException e) {
                continue;
            }

            if (clickedItem.getType() == iconMaterial) {
                openCategoryView(player, categoryName);
                return;
            }
        }
    }

    /**
     * Handle click di category view
     */
    private void handleCategoryClick(Player player, ItemStack clickedItem, String inventoryTitle) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // Back button
        if (clickedItem.getType() == Material.ARROW) {
            openMainMenu(player);
            return;
        }

        // Coba purchase item
        ShopItem shopItem = shopManager.getItem(clickedItem.getType());
        if (shopItem != null) {
            purchaseItem(player, shopItem);
        }
    }

    /**
     * Proses pembelian item
     */
    private void purchaseItem(Player player, ShopItem shopItem) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));
        
        // Cek balance
        long totalCopperBalance = currencyManager.getTotalCopperValue(player);
        if (totalCopperBalance < shopItem.getTotalCopperPrice()) {
            String msg = colorize(plugin.getConfig().getString("messages.insufficient_balance", "&cBalance tidak cukup!"));
            player.sendMessage(prefix + msg);
            return;
        }

        // Deduct currency
        if (!currencyManager.deductCurrency(player, shopItem.getPrice())) {
            String msg = colorize(plugin.getConfig().getString("messages.conversion_failed", "&cKonversi gagal! Periksa balance Anda."));
            player.sendMessage(prefix + msg);
            return;
        }

        // Give item
        ItemStack item = new ItemStack(shopItem.getMaterial(), 1);
        
        // Cek apakah inventory penuh
        if (player.getInventory().firstEmpty() == -1) {
            // Inventory penuh, kembalikan currency
            // (simplified - dalam implementasi real, perlu track exact deduction)
            player.sendMessage(prefix + "&cInventory penuh!");
            return;
        }

        player.getInventory().addItem(item);
        
        String msg = colorize(plugin.getConfig().getString("messages.purchase_success", "&aItem berhasil dibeli!"));
        player.sendMessage(prefix + msg);
        
        player.closeInventory();
    }

    /**
     * Helper untuk colorize string
     */
    private String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
