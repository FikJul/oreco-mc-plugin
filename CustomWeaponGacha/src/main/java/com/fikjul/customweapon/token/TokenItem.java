package com.fikjul.customweapon.token;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class untuk membuat Gacha Token item
 */
public class TokenItem {
    
    private final CustomWeaponPlugin plugin;
    private final NamespacedKey tokenKey;

    public TokenItem(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.tokenKey = new NamespacedKey(plugin, "gacha_token");
    }

    /**
     * Create Gacha Token item
     */
    public ItemStack createToken(int amount) {
        ItemStack token = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = token.getItemMeta();

        if (meta != null) {
            // Set display name
            meta.setDisplayName(MessageUtil.color("&d&lGacha Token"));

            // Set lore
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7Use this token to roll"));
            lore.add(MessageUtil.color("&7in the Weapon Gacha!"));
            lore.add(MessageUtil.color(""));
            lore.add(MessageUtil.color("&eRight-click to add to balance"));
            meta.setLore(lore);

            // Add glow effect
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Set custom NBT tag untuk identifikasi
            meta.getPersistentDataContainer().set(tokenKey, PersistentDataType.BYTE, (byte) 1);

            token.setItemMeta(meta);
        }

        return token;
    }

    /**
     * Check apakah item adalah Gacha Token
     */
    public boolean isToken(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(tokenKey, PersistentDataType.BYTE);
    }

    /**
     * Get amount dari token item
     */
    public int getTokenAmount(ItemStack item) {
        if (!isToken(item)) {
            return 0;
        }
        return item.getAmount();
    }

    /**
     * Create token untuk boss drops
     */
    public ItemStack createBossDropToken(int amount) {
        // Boss drop token sama dengan token biasa
        return createToken(amount);
    }
}
