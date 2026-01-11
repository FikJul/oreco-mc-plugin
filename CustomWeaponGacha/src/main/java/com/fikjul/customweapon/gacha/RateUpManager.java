package com.fikjul.customweapon.gacha;

import com.fikjul.customweapon.CustomWeaponPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager untuk rate-up events
 */
public class RateUpManager {
    
    private final CustomWeaponPlugin plugin;
    
    // Banner -> Rarity -> Multiplier & Expiry
    private final Map<String, Map<String, RateUpEvent>> activeRateUps;

    public RateUpManager(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.activeRateUps = new HashMap<>();
        
        // Start cleanup task
        startCleanupTask();
    }

    /**
     * Activate rate-up event
     */
    public void activateRateUp(String banner, String rarity, double multiplier, long durationSeconds) {
        activeRateUps.putIfAbsent(banner, new HashMap<>());
        
        long expiryTime = System.currentTimeMillis() + (durationSeconds * 1000);
        RateUpEvent event = new RateUpEvent(multiplier, expiryTime);
        
        activeRateUps.get(banner).put(rarity.toUpperCase(), event);
        
        plugin.getLogger().info("Rate-up activated: " + banner + " - " + rarity + " x" + multiplier + 
                " for " + durationSeconds + "s");
    }

    /**
     * Get rate multiplier untuk banner dan rarity
     */
    public double getRateMultiplier(String banner, String rarity) {
        if (!activeRateUps.containsKey(banner)) {
            return 1.0;
        }
        
        Map<String, RateUpEvent> bannerRateUps = activeRateUps.get(banner);
        RateUpEvent event = bannerRateUps.get(rarity.toUpperCase());
        
        if (event == null || event.isExpired()) {
            return 1.0;
        }
        
        return event.multiplier;
    }

    /**
     * Check apakah ada active rate-up
     */
    public boolean hasActiveRateUp(String banner, String rarity) {
        if (!activeRateUps.containsKey(banner)) {
            return false;
        }
        
        RateUpEvent event = activeRateUps.get(banner).get(rarity.toUpperCase());
        return event != null && !event.isExpired();
    }

    /**
     * Get remaining time untuk rate-up
     */
    public long getRemainingTime(String banner, String rarity) {
        if (!hasActiveRateUp(banner, rarity)) {
            return 0;
        }
        
        RateUpEvent event = activeRateUps.get(banner).get(rarity.toUpperCase());
        return Math.max(0, event.expiryTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * Deactivate rate-up
     */
    public void deactivateRateUp(String banner, String rarity) {
        if (activeRateUps.containsKey(banner)) {
            activeRateUps.get(banner).remove(rarity.toUpperCase());
        }
    }

    /**
     * Clear all rate-ups untuk banner
     */
    public void clearBannerRateUps(String banner) {
        activeRateUps.remove(banner);
    }

    /**
     * Clear all rate-ups
     */
    public void clearAllRateUps() {
        activeRateUps.clear();
    }

    /**
     * Get all active rate-ups
     */
    public Map<String, Map<String, RateUpEvent>> getActiveRateUps() {
        // Return copy to prevent modification
        Map<String, Map<String, RateUpEvent>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, RateUpEvent>> entry : activeRateUps.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Cleanup expired rate-ups
     */
    private void startCleanupTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            
            for (Map<String, RateUpEvent> bannerRateUps : activeRateUps.values()) {
                bannerRateUps.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
            }
            
            activeRateUps.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        }, 1200L, 1200L); // Every minute
    }

    /**
     * Inner class untuk rate-up event
     */
    public static class RateUpEvent {
        public final double multiplier;
        public final long expiryTime;

        public RateUpEvent(double multiplier, long expiryTime) {
            this.multiplier = multiplier;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiryTime;
        }

        public boolean isExpired(long now) {
            return now >= expiryTime;
        }
    }
}
