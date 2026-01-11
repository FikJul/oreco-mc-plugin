package com.fikjul.customweapon.weapon;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.utils.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager untuk mengelola weapon instances dan tracking
 */
public class WeaponManager {
    
    private final CustomWeaponPlugin plugin;
    private final CooldownManager cooldownManager;
    
    // Track combo untuk Astral Mark
    private final Map<UUID, Map<UUID, ComboTracker>> comboTrackers;  // Attacker -> Target -> Tracker

    public WeaponManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.cooldownManager = new CooldownManager();
        this.comboTrackers = new HashMap<>();
        
        // Start cleanup task untuk cooldowns dan combos
        startCleanupTask();
    }

    /**
     * Create weapon item untuk player (dengan owner UUID untuk soulbound)
     */
    public ItemStack createWeaponItem(String weaponId, UUID ownerUUID) {
        CustomWeapon weapon = plugin.getWeaponRegistry().getWeapon(weaponId);
        if (weapon == null) {
            return null;
        }

        return weapon.createItemStack(plugin.getWeaponRegistry().getWeaponKey(), ownerUUID);
    }

    /**
     * Give weapon ke player
     */
    public boolean giveWeapon(Player player, String weaponId) {
        ItemStack weapon = createWeaponItem(weaponId, player.getUniqueId());
        if (weapon == null) {
            return false;
        }

        // Try add to inventory
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(weapon);
            return true;
        } else {
            // Drop di ground jika inventory full
            player.getWorld().dropItemNaturally(player.getLocation(), weapon);
            return true;
        }
    }

    /**
     * Get CooldownManager
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /**
     * Track combo hit untuk Astral Mark
     */
    public void trackCombo(Player attacker, UUID targetUUID, int requiredHits) {
        UUID attackerUUID = attacker.getUniqueId();
        
        comboTrackers.putIfAbsent(attackerUUID, new HashMap<>());
        Map<UUID, ComboTracker> attackerCombos = comboTrackers.get(attackerUUID);
        
        ComboTracker tracker = attackerCombos.get(targetUUID);
        if (tracker == null) {
            tracker = new ComboTracker(requiredHits, 2000); // 2 second timeout
            attackerCombos.put(targetUUID, tracker);
        }
        
        tracker.hit();
    }

    /**
     * Check apakah combo complete
     */
    public boolean isComboComplete(Player attacker, UUID targetUUID) {
        UUID attackerUUID = attacker.getUniqueId();
        
        if (!comboTrackers.containsKey(attackerUUID)) {
            return false;
        }
        
        Map<UUID, ComboTracker> attackerCombos = comboTrackers.get(attackerUUID);
        ComboTracker tracker = attackerCombos.get(targetUUID);
        
        return tracker != null && tracker.isComplete();
    }

    /**
     * Reset combo tracker
     */
    public void resetCombo(Player attacker, UUID targetUUID) {
        UUID attackerUUID = attacker.getUniqueId();
        
        if (comboTrackers.containsKey(attackerUUID)) {
            comboTrackers.get(attackerUUID).remove(targetUUID);
        }
    }

    /**
     * Get combo hits count
     */
    public int getComboHits(Player attacker, UUID targetUUID) {
        UUID attackerUUID = attacker.getUniqueId();
        
        if (!comboTrackers.containsKey(attackerUUID)) {
            return 0;
        }
        
        Map<UUID, ComboTracker> attackerCombos = comboTrackers.get(attackerUUID);
        ComboTracker tracker = attackerCombos.get(targetUUID);
        
        return tracker != null ? tracker.getHits() : 0;
    }

    /**
     * Cleanup task untuk performance
     */
    private void startCleanupTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // Cleanup cooldowns
            cooldownManager.cleanup();
            
            // Cleanup expired combos
            long now = System.currentTimeMillis();
            for (Map<UUID, ComboTracker> attackerCombos : comboTrackers.values()) {
                attackerCombos.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
            }
            comboTrackers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            
        }, 600L, 600L); // Run every 30 seconds (600 ticks)
    }

    /**
     * Inner class untuk tracking combo hits
     */
    private static class ComboTracker {
        private int hits;
        private final int requiredHits;
        private long lastHitTime;
        private final long timeout;

        public ComboTracker(int requiredHits, long timeout) {
            this.hits = 0;
            this.requiredHits = requiredHits;
            this.lastHitTime = System.currentTimeMillis();
            this.timeout = timeout;
        }

        public void hit() {
            long now = System.currentTimeMillis();
            
            // Reset jika timeout
            if (now - lastHitTime > timeout) {
                hits = 0;
            }
            
            hits++;
            lastHitTime = now;
        }

        public boolean isComplete() {
            // Check timeout
            if (System.currentTimeMillis() - lastHitTime > timeout) {
                hits = 0;
                return false;
            }
            return hits >= requiredHits;
        }

        public int getHits() {
            // Check timeout
            if (System.currentTimeMillis() - lastHitTime > timeout) {
                hits = 0;
            }
            return hits;
        }

        public boolean isExpired(long now) {
            return now - lastHitTime > timeout * 2; // Cleanup after 2x timeout
        }
    }
}
