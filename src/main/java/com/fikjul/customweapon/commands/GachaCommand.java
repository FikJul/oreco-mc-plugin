package com.fikjul.customweapon.commands;

import com.fikjul.customweapon.CustomWeaponPlugin;
import com.fikjul.customweapon.collection.CollectionGUI;
import com.fikjul.customweapon.gacha.GachaAnimation;
import com.fikjul.customweapon.gacha.GachaGUI;
import com.fikjul.customweapon.gacha.GachaManager;
import com.fikjul.customweapon.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler untuk /gacha
 */
public class GachaCommand implements CommandExecutor, TabCompleter {
    
    private final CustomWeaponPlugin plugin;
    private final GachaGUI gachaGUI;
    private final CollectionGUI collectionGUI;
    private final GachaAnimation animation;

    public GachaCommand(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
        this.gachaGUI = new GachaGUI(plugin);
        this.collectionGUI = new CollectionGUI(plugin);
        this.animation = new GachaAnimation(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // /gacha - Open main menu
        if (args.length == 0) {
            gachaGUI.openGachaMenu(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "balance":
            case "bal":
                handleBalance(player);
                break;

            case "buytoken":
            case "buy":
                handleBuyToken(player, args);
                break;

            case "collection":
                handleCollection(player);
                break;

            case "roll":
                handleRoll(player, args);
                break;

            case "admin":
                handleAdmin(player, args);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    /**
     * Handle /gacha balance
     */
    private void handleBalance(Player player) {
        int balance = plugin.getTokenManager().getBalance(player);
        MessageUtil.send(player, "&7Token Balance: &d" + balance + " Token");
    }

    /**
     * Handle /gacha buytoken <amount>
     */
    private void handleBuyToken(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(player, "&cUsage: /gacha buytoken <amount>");
            return;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                MessageUtil.send(player, "&cAmount must be positive!");
                return;
            }

            plugin.getTokenManager().purchaseTokens(player, amount);
        } catch (NumberFormatException e) {
            MessageUtil.send(player, "&cInvalid amount!");
        }
    }

    /**
     * Handle /gacha collection
     */
    private void handleCollection(Player player) {
        collectionGUI.openCollectionGUI(player);
    }

    /**
     * Handle /gacha roll <banner> [10x]
     */
    private void handleRoll(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(player, "&cUsage: /gacha roll <banner> [10x]");
            return;
        }

        String bannerId = args[1];
        boolean multi = args.length > 2 && args[2].equalsIgnoreCase("10x");

        if (multi) {
            // 10x roll
            var results = plugin.getGachaManager().rollMulti(player, bannerId);
            if (results != null) {
                animation.playMultiAnimation(player, results);
                
                // Give items
                for (var result : results) {
                    if (result.rewardItem != null) {
                        player.getInventory().addItem(result.rewardItem);
                        
                        // Add to collection if weapon
                        if (result.dropItem != null && 
                            result.dropItem.type == com.fikjul.customweapon.gacha.GachaBanner.DropItemType.WEAPON) {
                            plugin.getCollectionManager().addWeapon(player, result.dropItem.weaponId);
                        }
                    }
                }
            }
        } else {
            // Single roll
            var result = plugin.getGachaManager().rollSingle(player, bannerId);
            if (result != null) {
                animation.playAnimation(player, result);
                
                // Give item
                if (result.rewardItem != null) {
                    player.getInventory().addItem(result.rewardItem);
                    
                    // Add to collection if weapon
                    if (result.dropItem != null && 
                        result.dropItem.type == com.fikjul.customweapon.gacha.GachaBanner.DropItemType.WEAPON) {
                        plugin.getCollectionManager().addWeapon(player, result.dropItem.weaponId);
                    }
                }
            }
        }
    }

    /**
     * Handle /gacha admin
     */
    private void handleAdmin(Player player, String[] args) {
        if (!player.hasPermission("customweapon.admin")) {
            MessageUtil.send(player, "&cYou don't have permission!");
            return;
        }

        if (args.length < 2) {
            MessageUtil.send(player, "&cUsage: /gacha admin <reload|rateup>");
            return;
        }

        String adminCmd = args[1].toLowerCase();

        if (adminCmd.equals("reload")) {
            plugin.getConfigManager().reloadAll();
            plugin.getWeaponRegistry().reload();
            plugin.getGachaManager().reload();
            MessageUtil.send(player, "&aConfiguration reloaded!");
        } else if (adminCmd.equals("rateup")) {
            // /gacha admin rateup <banner> <rarity> <multiplier> <duration>
            if (args.length < 6) {
                MessageUtil.send(player, "&cUsage: /gacha admin rateup <banner> <rarity> <multiplier> <duration>");
                return;
            }

            try {
                String banner = args[2];
                String rarity = args[3];
                double multiplier = Double.parseDouble(args[4]);
                long duration = parseDuration(args[5]);

                plugin.getRateUpManager().activateRateUp(banner, rarity, multiplier, duration);
                MessageUtil.send(player, "&aRate-up activated: " + banner + " - " + rarity + 
                        " x" + multiplier + " for " + args[5]);
            } catch (NumberFormatException e) {
                MessageUtil.send(player, "&cInvalid numbers!");
            }
        }
    }

    /**
     * Parse duration string (e.g., "7d", "24h", "30m")
     */
    private long parseDuration(String duration) {
        String unit = duration.substring(duration.length() - 1);
        long value = Long.parseLong(duration.substring(0, duration.length() - 1));

        return switch (unit.toLowerCase()) {
            case "d" -> value * 86400; // days
            case "h" -> value * 3600;  // hours
            case "m" -> value * 60;    // minutes
            default -> value;          // seconds
        };
    }

    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        MessageUtil.send(player, "&6&l═══ Gacha Commands ═══");
        MessageUtil.send(player, "&e/gacha &7- Open gacha menu");
        MessageUtil.send(player, "&e/gacha balance &7- Check token balance");
        MessageUtil.send(player, "&e/gacha buytoken <amount> &7- Buy tokens with diamonds");
        MessageUtil.send(player, "&e/gacha collection &7- View weapon collection");
        MessageUtil.send(player, "&e/gacha roll <banner> [10x] &7- Roll gacha");
        
        if (player.hasPermission("customweapon.admin")) {
            MessageUtil.send(player, "&c/gacha admin reload &7- Reload configs");
            MessageUtil.send(player, "&c/gacha admin rateup <banner> <rarity> <mult> <dur>");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("balance", "buytoken", "collection", "roll", "admin"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("roll")) {
                // Add banner names
                for (var banner : plugin.getGachaManager().getEnabledBanners()) {
                    completions.add(banner.getId());
                }
            } else if (args[0].equalsIgnoreCase("admin")) {
                completions.addAll(Arrays.asList("reload", "rateup"));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("roll")) {
            completions.add("10x");
        }

        return completions;
    }
}
