package com.fikjul.customweapon.listeners;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener untuk pickup/redeem token items
 */
public class TokenPickupListener implements Listener {
    
    private final CustomWeaponPlugin plugin;

    public TokenPickupListener(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTokenRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        // Check if it's a token
        if (plugin.getTokenManager().isTokenItem(item)) {
            // Redeem token
            if (plugin.getTokenManager().redeemTokenItem(player, item)) {
                event.setCancelled(true);
            }
        }
    }
}
