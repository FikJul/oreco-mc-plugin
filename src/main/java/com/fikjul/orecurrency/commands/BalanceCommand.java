package com.fikjul.orecurrency.commands;

import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyManager;
import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Command executor untuk /balance (/bal)
 */
public class BalanceCommand implements CommandExecutor {
    private final OrecoPlugin plugin;
    private final CurrencyManager currencyManager;

    public BalanceCommand(OrecoPlugin plugin) {
        this.plugin = plugin;
        this.currencyManager = plugin.getCurrencyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        
        // Get balance dari enderchest
        Map<CurrencyType, Integer> balance = currencyManager.getBalance(player);
        long totalCopper = currencyManager.getTotalCopperValue(player);

        // Format dan kirim message
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));
        
        player.sendMessage(colorize(prefix + "&6&l===== Your Balance ====="));
        
        // Tampilkan breakdown per currency
        for (CurrencyType type : CurrencyType.values()) {
            int amount = balance.get(type);
            if (amount > 0) {
                String line = String.format("&e%s: &f%d", type.getDisplayName(), amount);
                player.sendMessage(colorize(prefix + line));
            }
        }

        // Tampilkan total dalam copper equivalent
        player.sendMessage(colorize(prefix + "&7Total Value: &f" + totalCopper + " &7copper"));
        player.sendMessage(colorize(prefix + "&6&l========================"));

        return true;
    }

    private String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
