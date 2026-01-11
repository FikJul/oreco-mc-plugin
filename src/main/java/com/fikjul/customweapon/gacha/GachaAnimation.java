package com.fikjul.customweapon.gacha;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.entity.Player;

/**
 * Animation untuk slot machine gacha (simplified stub)
 */
public class GachaAnimation {
    
    private final CustomWeaponPlugin plugin;

    public GachaAnimation(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Play gacha animation untuk single roll
     */
    public void playAnimation(Player player, GachaManager.GachaResult result) {
        // TODO: Implement spinning slot machine animation
        // - Create GUI inventory
        // - Animate spinning items
        // - Play sounds and particles
        // - Reveal result
        
        // For now, just show result
        new GachaGUI(plugin).playSlotAnimation(player, result);
    }

    /**
     * Play animation untuk multi roll (10x)
     */
    public void playMultiAnimation(Player player, java.util.List<GachaManager.GachaResult> results) {
        // TODO: Implement multi-roll animation
        
        // For now, just show results
        player.sendMessage("§6§l═══════ 10x ROLL RESULTS ═══════");
        for (int i = 0; i < results.size(); i++) {
            var result = results.get(i);
            String rarityColor = result.rarity.getColor();
            player.sendMessage("§7" + (i + 1) + ". " + rarityColor + result.rarity.getDisplayName());
        }
    }
}
