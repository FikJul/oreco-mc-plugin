package com.fikjul.orecurrency.currency;

import com.fikjul.orecurrency.OrecoPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager untuk mengelola balance dan konversi mata uang
 * Balance dihitung dari isi Enderchest player secara real-time
 */
public class CurrencyManager {
    private final OrecoPlugin plugin;

    public CurrencyManager(OrecoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Mendapatkan balance player dari Enderchest
     * @return Map dengan currency type sebagai key dan jumlah sebagai value
     */
    public Map<CurrencyType, Integer> getBalance(Player player) {
        Map<CurrencyType, Integer> balance = new HashMap<>();
        Inventory enderchest = player.getEnderChest();

        // Inisialisasi semua currency dengan 0
        for (CurrencyType type : CurrencyType.values()) {
            balance.put(type, 0);
        }

        // Scan enderchest dan hitung items
        for (ItemStack item : enderchest.getContents()) {
            if (item == null) continue;
            
            CurrencyType type = CurrencyType.fromMaterial(item.getType());
            if (type != null) {
                balance.put(type, balance.get(type) + item.getAmount());
            }
        }

        return balance;
    }

    /**
     * Mendapatkan total balance dalam copper equivalent
     */
    public long getTotalCopperValue(Player player) {
        Map<CurrencyType, Integer> balance = getBalance(player);
        long total = 0;

        for (Map.Entry<CurrencyType, Integer> entry : balance.entrySet()) {
            total += entry.getKey().getCopperValue() * entry.getValue();
        }

        return total;
    }

    /**
     * Cek apakah konversi valid berdasarkan config
     */
    public boolean canConvert(CurrencyType from, CurrencyType to) {
        String rule1 = from.name().toLowerCase() + "_" + to.name().toLowerCase();
        String rule2 = to.name().toLowerCase() + "_" + from.name().toLowerCase();

        // Cek apakah ada aturan konversi
        if (plugin.getConfig().isSet("currency.conversion_rules." + rule1)) {
            return plugin.getConfig().getBoolean("currency.conversion_rules." + rule1);
        }
        if (plugin.getConfig().isSet("currency.conversion_rules." + rule2)) {
            return plugin.getConfig().getBoolean("currency.conversion_rules." + rule2);
        }

        return false;
    }

    /**
     * Konversi mata uang dari enderchest player
     * @return true jika konversi berhasil
     */
    public boolean convert(Player player, int amount, CurrencyType from, CurrencyType to) {
        // Validasi konversi
        if (!canConvert(from, to)) {
            return false;
        }

        // Hitung jumlah yang dibutuhkan
        long fromValue = from.getCopperValue() * amount;
        long toValue = to.getCopperValue();

        // Pastikan bisa dibagi habis
        if (fromValue % toValue != 0) {
            return false;
        }

        int toAmount = (int) (fromValue / toValue);

        // Cek apakah player punya cukup currency
        Map<CurrencyType, Integer> balance = getBalance(player);
        if (balance.get(from) < amount) {
            return false;
        }

        // Cek apakah ada space di enderchest
        Inventory enderchest = player.getEnderChest();
        
        // Remove source currency
        int remaining = amount;
        for (int i = 0; i < enderchest.getSize() && remaining > 0; i++) {
            ItemStack item = enderchest.getItem(i);
            if (item != null && item.getType() == from.getMaterial()) {
                int removeAmount = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - removeAmount);
                if (item.getAmount() <= 0) {
                    enderchest.setItem(i, null);
                }
                remaining -= removeAmount;
            }
        }

        // Add target currency
        ItemStack toAdd = new ItemStack(to.getMaterial(), toAmount);
        HashMap<Integer, ItemStack> overflow = enderchest.addItem(toAdd);
        
        // Jika ada overflow, batalkan transaksi
        if (!overflow.isEmpty()) {
            // Kembalikan currency yang sudah diambil
            ItemStack toReturn = new ItemStack(from.getMaterial(), amount);
            enderchest.addItem(toReturn);
            return false;
        }

        return true;
    }

    /**
     * Deduct currency dari enderchest untuk pembayaran
     * Menggunakan smart deduction - ambil dari currency terkecil dulu
     * @param prices Map dengan currency type dan jumlah yang dibutuhkan
     * @return true jika berhasil
     */
    public boolean deductCurrency(Player player, Map<CurrencyType, Integer> prices) {
        Inventory enderchest = player.getEnderChest();
        Map<CurrencyType, Integer> balance = getBalance(player);

        // Hitung total copper yang dibutuhkan
        long totalRequired = 0;
        for (Map.Entry<CurrencyType, Integer> entry : prices.entrySet()) {
            totalRequired += entry.getKey().getCopperValue() * entry.getValue();
        }

        // Cek apakah player punya cukup balance
        if (getTotalCopperValue(player) < totalRequired) {
            return false;
        }

        // Coba deduct exact currencies dulu
        Map<CurrencyType, Integer> toDeduct = new HashMap<>(prices);
        
        // Cek apakah semua currency tersedia dalam jumlah yang diminta
        boolean canDeductExact = true;
        for (Map.Entry<CurrencyType, Integer> entry : toDeduct.entrySet()) {
            if (balance.get(entry.getKey()) < entry.getValue()) {
                canDeductExact = false;
                break;
            }
        }

        if (canDeductExact) {
            // Deduct exact currencies
            for (Map.Entry<CurrencyType, Integer> entry : toDeduct.entrySet()) {
                removeFromEnderchest(enderchest, entry.getKey(), entry.getValue());
            }
            return true;
        } else {
            // Smart deduction - ambil dari currency terkecil
            long remaining = totalRequired;
            Map<CurrencyType, Integer> actualDeduction = new HashMap<>();

            // Mulai dari currency terkecil
            for (CurrencyType type : CurrencyType.values()) {
                if (remaining <= 0) break;
                
                int available = balance.get(type);
                if (available > 0) {
                    long typeValue = type.getCopperValue();
                    int needed = (int) Math.min(available, (remaining + typeValue - 1) / typeValue);
                    
                    if (needed > 0) {
                        actualDeduction.put(type, needed);
                        remaining -= typeValue * needed;
                    }
                }
            }

            // Jika masih kurang, coba dari currency yang lebih besar
            if (remaining > 0) {
                return false;
            }

            // Deduct currencies
            for (Map.Entry<CurrencyType, Integer> entry : actualDeduction.entrySet()) {
                removeFromEnderchest(enderchest, entry.getKey(), entry.getValue());
            }

            return true;
        }
    }

    /**
     * Helper method untuk remove items dari enderchest
     */
    private void removeFromEnderchest(Inventory enderchest, CurrencyType type, int amount) {
        int remaining = amount;
        for (int i = 0; i < enderchest.getSize() && remaining > 0; i++) {
            ItemStack item = enderchest.getItem(i);
            if (item != null && item.getType() == type.getMaterial()) {
                int removeAmount = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - removeAmount);
                if (item.getAmount() <= 0) {
                    enderchest.setItem(i, null);
                }
                remaining -= removeAmount;
            }
        }
    }

    /**
     * Format balance untuk ditampilkan
     */
    public String formatBalance(Map<CurrencyType, Integer> balance) {
        StringBuilder sb = new StringBuilder();
        
        for (CurrencyType type : CurrencyType.values()) {
            int amount = balance.get(type);
            if (amount > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(amount).append(" ").append(type.getDisplayName());
            }
        }

        if (sb.length() == 0) {
            sb.append("0 Currency");
        }

        return sb.toString();
    }
}
