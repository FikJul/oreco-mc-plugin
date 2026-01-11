package com.fikjul.orecurrency.commands;

import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyManager;
import com.fikjul.orecurrency.currency.CurrencyType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor untuk /currency
 */
public class CurrencyCommand implements CommandExecutor {
    private final OrecoPlugin plugin;
    private final CurrencyManager currencyManager;

    public CurrencyCommand(OrecoPlugin plugin) {
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
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        // /currency convert <amount> <from> <to>
        if (args.length < 4 || !args[0].equalsIgnoreCase("convert")) {
            player.sendMessage(colorize(prefix + "&cUsage: /currency convert <amount> <from> <to>"));
            return true;
        }

        // Parse amount
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage(colorize(prefix + "&cAmount harus lebih dari 0!"));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(colorize(prefix + "&cAmount harus berupa angka!"));
            return true;
        }

        // Parse currency types
        CurrencyType from = CurrencyType.fromString(args[2]);
        CurrencyType to = CurrencyType.fromString(args[3]);

        if (from == null) {
            player.sendMessage(colorize(prefix + "&cCurrency tidak valid: " + args[2]));
            player.sendMessage(colorize(prefix + "&7Valid currencies: copper, iron, gold, emerald, diamond, netherite"));
            return true;
        }

        if (to == null) {
            player.sendMessage(colorize(prefix + "&cCurrency tidak valid: " + args[3]));
            player.sendMessage(colorize(prefix + "&7Valid currencies: copper, iron, gold, emerald, diamond, netherite"));
            return true;
        }

        if (from == to) {
            player.sendMessage(colorize(prefix + "&cTidak bisa convert currency yang sama!"));
            return true;
        }

        // Cek apakah konversi diperbolehkan
        if (!currencyManager.canConvert(from, to)) {
            player.sendMessage(colorize(prefix + "&cKonversi dari " + from.getDisplayName() + " ke " + to.getDisplayName() + " tidak diperbolehkan!"));
            return true;
        }

        // Attempt conversion
        if (currencyManager.convert(player, amount, from, to)) {
            String msg = colorize(plugin.getConfig().getString("messages.conversion_success", "&aKonversi berhasil!"));
            player.sendMessage(colorize(prefix + msg));
            
            // Hitung hasil konversi
            long fromValue = from.getCopperValue() * amount;
            int toAmount = (int) (fromValue / to.getCopperValue());
            
            player.sendMessage(colorize(prefix + "&7" + amount + " " + from.getDisplayName() + " → " + toAmount + " " + to.getDisplayName()));
        } else {
            String msg = colorize(plugin.getConfig().getString("messages.conversion_failed", "&cKonversi gagal! Periksa balance Anda."));
            player.sendMessage(colorize(prefix + msg));
        }

        return true;
    }

    private String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
