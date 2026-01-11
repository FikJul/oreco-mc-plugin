package com.fikjul.customweapon.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Utility class untuk mengirim messages ke players
 */
public class MessageUtil {
    
    /**
     * Color code message dengan & notation
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Send colored message ke player
     */
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    /**
     * Send message dengan prefix dari config
     */
    public static void send(CommandSender sender, FileConfiguration config, String path) {
        String prefix = config.getString("messages.prefix", "&8[&dGacha&8] &r");
        String message = config.getString("messages." + path, path);
        sender.sendMessage(color(prefix + message));
    }

    /**
     * Send message dengan placeholder replacement
     */
    public static void send(CommandSender sender, String message, String... replacements) {
        String colored = color(message);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                colored = colored.replace(replacements[i], replacements[i + 1]);
            }
        }
        sender.sendMessage(colored);
    }

    /**
     * Broadcast message ke semua online players
     */
    public static void broadcast(String message) {
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            send(player, message);
        }
    }

    /**
     * Format number dengan separator
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    /**
     * Format time duration (seconds to human readable)
     */
    public static String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "h " + minutes + "m";
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "d " + hours + "h";
        }
    }
}
