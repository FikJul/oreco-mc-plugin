package com.fikjul.customweapon.gacha;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.entity.Player;

/**
 * GUI untuk gacha system (simplified stub)
 */
public class GachaGUI {
    
    private final CustomWeaponPlugin plugin;

    public GachaGUI(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open main gacha menu
     */
    public void openGachaMenu(Player player) {
        // TODO: Implement gacha GUI with banners selection
        player.sendMessage("§6§l═══════ GACHA MENU ═══════");
        player.sendMessage("§7Token Balance: §d" + plugin.getTokenManager().getBalance(player));
        player.sendMessage("");
        
        var banners = plugin.getGachaManager().getEnabledBanners();
        player.sendMessage("§eAvailable Banners:");
        for (var banner : banners) {
            player.sendMessage("  §7- " + banner.getDisplayName() + 
                    " §8(§e" + banner.getCostSingle() + " token§8)");
        }
        
        player.sendMessage("");
        player.sendMessage("§7Use §e/gacha roll <banner> §7to roll!");
    }

    /**
     * Play slot machine animation
     */
    public void playSlotAnimation(Player player, GachaManager.GachaResult result) {
        // TODO: Implement slot machine animation
        // For now, just send result message
        String rarityColor = result.rarity.getColor();
        player.sendMessage(rarityColor + "✦ " + result.rarity.getDisplayName() + " ✦");
        
        if (result.rewardItem != null && result.rewardItem.hasItemMeta()) {
            player.sendMessage("§7You got: " + rarityColor + result.rewardItem.getItemMeta().getDisplayName());
        }
    }
}
