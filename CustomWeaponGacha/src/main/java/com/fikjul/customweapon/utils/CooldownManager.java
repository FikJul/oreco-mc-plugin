package com.fikjul.customweapon.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager untuk cooldown abilities dan actions
 */
public class CooldownManager {
    
    // Map: PlayerUUID -> (AbilityName -> ExpireTime)
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    /**
     * Set cooldown untuk player dan ability
     */
    public void setCooldown(Player player, String ability, int seconds) {
        UUID uuid = player.getUniqueId();
        cooldowns.putIfAbsent(uuid, new HashMap<>());
        
        long expireTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.get(uuid).put(ability, expireTime);
    }

    /**
     * Check apakah player masih dalam cooldown
     */
    public boolean isOnCooldown(Player player, String ability) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return false;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(ability)) return false;
        
        long expireTime = playerCooldowns.get(ability);
        if (System.currentTimeMillis() >= expireTime) {
            playerCooldowns.remove(ability);
            return false;
        }
        
        return true;
    }

    /**
     * Get remaining cooldown time dalam detik
     */
    public int getRemainingCooldown(Player player, String ability) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return 0;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(ability)) return 0;
        
        long expireTime = playerCooldowns.get(ability);
        long remaining = expireTime - System.currentTimeMillis();
        
        if (remaining <= 0) {
            playerCooldowns.remove(ability);
            return 0;
        }
        
        return (int) Math.ceil(remaining / 1000.0);
    }

    /**
     * Clear all cooldowns untuk player
     */
    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    /**
     * Clear specific cooldown
     */
    public void clearCooldown(Player player, String ability) {
        UUID uuid = player.getUniqueId();
        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).remove(ability);
        }
    }

    /**
     * Extend cooldown dengan jumlah tertentu (untuk Time Fracture ability)
     */
    public void extendCooldown(Player player, String ability, int seconds) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(ability)) return;
        
        long currentExpire = playerCooldowns.get(ability);
        long newExpire = currentExpire + (seconds * 1000L);
        playerCooldowns.put(ability, newExpire);
    }

    /**
     * Get semua active cooldowns untuk player
     */
    public Map<String, Integer> getActiveCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> active = new HashMap<>();
        
        if (!cooldowns.containsKey(uuid)) return active;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        long now = System.currentTimeMillis();
        
        for (Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
            long remaining = entry.getValue() - now;
            if (remaining > 0) {
                active.put(entry.getKey(), (int) Math.ceil(remaining / 1000.0));
            }
        }
        
        return active;
    }

    /**
     * Cleanup expired cooldowns (untuk performance)
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        
        for (Map<String, Long> playerCooldowns : cooldowns.values()) {
            playerCooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
        }
        
        cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
