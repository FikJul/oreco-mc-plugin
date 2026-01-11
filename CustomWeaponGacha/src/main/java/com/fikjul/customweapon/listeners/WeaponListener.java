package com.fikjul.customweapon.listeners;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.weapon.CustomWeapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Listener untuk weapon abilities dan effects
 * Simplified version - handles basic soulbound and ability triggers
 */
public class WeaponListener implements Listener {
    
    private final CustomWeaponPlugin plugin;

    public WeaponListener(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        CustomWeapon customWeapon = plugin.getWeaponRegistry().getWeaponFromItem(weapon);
        if (customWeapon == null) {
            return;
        }

        // Apply damage multiplier
        double damage = event.getDamage();
        
        // Check soulbound penalty
        if (customWeapon.isSoulboundEnabled()) {
            UUID owner = plugin.getWeaponRegistry().getWeaponOwner(weapon);
            if (owner != null && !owner.equals(attacker.getUniqueId())) {
                // Apply penalty
                int penalty = customWeapon.getOtherPlayerDamagePenalty();
                damage = damage * (1.0 - (penalty / 100.0));
            }
        }

        // Apply damage multiplier
        damage = damage * customWeapon.getDamageMultiplier();
        event.setDamage(damage);

        // TODO: Implement on-hit effects, combo tracking, etc.
        // This would require more complex logic for each weapon's effects
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack weapon = player.getInventory().getItemInMainHand();

        CustomWeapon customWeapon = plugin.getWeaponRegistry().getWeaponFromItem(weapon);
        if (customWeapon == null) {
            return;
        }

        // Check for right-click (ability activation)
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            // TODO: Implement active abilities
            // Check cooldown, trigger ability effects, etc.
        }
    }
}
