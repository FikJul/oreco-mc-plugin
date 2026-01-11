package com.fikjul.customweapon.listeners;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.utils.MessageUtil;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Listener untuk boss deaths dan token drops
 */
public class BossDeathListener implements Listener {
    
    private final CustomWeaponPlugin plugin;
    private final Random random;

    public BossDeathListener(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Boss)) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        // Check boss type
        if (event.getEntity() instanceof EnderDragon) {
            handleDragonDeath(killer, event);
        } else if (event.getEntity() instanceof Wither) {
            handleWitherDeath(killer, event);
        }
    }

    /**
     * Handle Ender Dragon death
     */
    private void handleDragonDeath(Player killer, EntityDeathEvent event) {
        var config = plugin.getConfigManager().getGachaConfig();
        
        if (!config.getBoolean("boss_drops.ender_dragon.enabled", true)) {
            return;
        }

        int chance = config.getInt("boss_drops.ender_dragon.chance", 20);
        if (random.nextInt(100) >= chance) {
            return; // No drop
        }

        int minAmount = config.getInt("boss_drops.ender_dragon.amount_min", 2);
        int maxAmount = config.getInt("boss_drops.ender_dragon.amount_max", 5);
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);

        // Drop tokens
        ItemStack token = plugin.getTokenManager().createTokenItem(amount);
        event.getDrops().add(token);

        // Broadcast
        if (config.getBoolean("boss_drops.ender_dragon.broadcast", true)) {
            String message = config.getString("boss_drops.ender_dragon.broadcast_message",
                    "&6[Gacha] &e{player} &7obtained &d{amount}x Gacha Token &7from Ender Dragon!");
            MessageUtil.broadcast(message
                    .replace("{player}", killer.getName())
                    .replace("{amount}", String.valueOf(amount)));
        }
    }

    /**
     * Handle Wither death
     */
    private void handleWitherDeath(Player killer, EntityDeathEvent event) {
        var config = plugin.getConfigManager().getGachaConfig();
        
        if (!config.getBoolean("boss_drops.wither.enabled", true)) {
            return;
        }

        int chance = config.getInt("boss_drops.wither.chance", 40);
        if (random.nextInt(100) >= chance) {
            return; // No drop
        }

        int minAmount = config.getInt("boss_drops.wither.amount_min", 1);
        int maxAmount = config.getInt("boss_drops.wither.amount_max", 3);
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);

        // Drop tokens
        ItemStack token = plugin.getTokenManager().createTokenItem(amount);
        event.getDrops().add(token);

        // Broadcast
        if (config.getBoolean("boss_drops.wither.broadcast", true)) {
            String message = config.getString("boss_drops.wither.broadcast_message",
                    "&6[Gacha] &e{player} &7obtained &d{amount}x Gacha Token &7from Wither!");
            MessageUtil.broadcast(message
                    .replace("{player}", killer.getName())
                    .replace("{amount}", String.valueOf(amount)));
        }
    }
}
