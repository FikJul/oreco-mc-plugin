package com.fikjul.orecurrency.shop;

import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class yang merepresentasikan item di shop
 */
public class ShopItem {
    private final Material material;
    private String displayName;
    private String category;
    private Map<CurrencyType, Integer> price;
    private List<String> lore;

    public ShopItem(Material material) {
        this.material = material;
        this.price = new HashMap<>();
        this.category = "Misc"; // Default category
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<CurrencyType, Integer> getPrice() {
        return price;
    }

    public void setPrice(Map<CurrencyType, Integer> price) {
        this.price = price;
    }

    public void addPrice(CurrencyType type, int amount) {
        this.price.put(type, amount);
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    /**
     * Mendapatkan total harga dalam copper equivalent
     */
    public long getTotalCopperPrice() {
        long total = 0;
        for (Map.Entry<CurrencyType, Integer> entry : price.entrySet()) {
            total += entry.getKey().getCopperValue() * entry.getValue();
        }
        return total;
    }

    /**
     * Format harga untuk ditampilkan
     */
    public String formatPrice() {
        if (price.isEmpty()) {
            return "Free";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<CurrencyType, Integer> entry : price.entrySet()) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(entry.getValue()).append(" ").append(entry.getKey().getDisplayName());
        }
        return sb.toString();
    }
}
