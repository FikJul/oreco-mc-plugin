package com.fikjul.customweapon.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Utility class untuk particle effects
 */
public class ParticleUtil {

    /**
     * Spawn particle di lokasi dengan count dan offset
     */
    public static void spawnParticle(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
        if (location.getWorld() == null) return;
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
    }

    /**
     * Spawn particle dengan speed
     */
    public static void spawnParticle(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        if (location.getWorld() == null) return;
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
    }

    /**
     * Spawn colored particle (REDSTONE)
     */
    public static void spawnColoredParticle(Location location, Color color, int count) {
        if (location.getWorld() == null) return;
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0f);
        location.getWorld().spawnParticle(Particle.DUST, location, count, 0.5, 0.5, 0.5, dustOptions);
    }

    /**
     * Parse hex color ke Bukkit Color
     */
    public static Color parseColor(String hex) {
        try {
            // Remove # if present
            hex = hex.replace("#", "");
            
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    /**
     * Create particle trail dari start ke end location
     */
    public static void createTrail(Location start, Location end, Particle particle, int points) {
        if (start.getWorld() == null || end.getWorld() == null) return;
        if (!start.getWorld().equals(end.getWorld())) return;

        double distance = start.distance(end);
        double dx = (end.getX() - start.getX()) / points;
        double dy = (end.getY() - start.getY()) / points;
        double dz = (end.getZ() - start.getZ()) / points;

        for (int i = 0; i < points; i++) {
            Location loc = start.clone().add(dx * i, dy * i, dz * i);
            spawnParticle(loc, particle, 1, 0, 0, 0);
        }
    }

    /**
     * Create particle circle di sekitar location
     */
    public static void createCircle(Location center, double radius, Particle particle, int points) {
        if (center.getWorld() == null) return;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            
            Location loc = center.clone().add(x, 0, z);
            spawnParticle(loc, particle, 1, 0, 0, 0);
        }
    }

    /**
     * Create particle sphere di sekitar location
     */
    public static void createSphere(Location center, double radius, Particle particle, int points) {
        if (center.getWorld() == null) return;

        for (int i = 0; i < points; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;
            
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            
            Location loc = center.clone().add(x, y, z);
            spawnParticle(loc, particle, 1, 0, 0, 0);
        }
    }

    /**
     * Show particle to specific player only
     */
    public static void showParticleToPlayer(Player player, Location location, Particle particle, int count) {
        player.spawnParticle(particle, location, count, 0.5, 0.5, 0.5);
    }
}
