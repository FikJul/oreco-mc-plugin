package com.fikjul.orecurrency.commands;

import com.fikjul.orecurrency.OrecoPlugin;
import com.fikjul.orecurrency.currency.CurrencyType;
import com.fikjul.orecurrency.shop.ShopGUI;
import com.fikjul.orecurrency.shop.ShopItem;
import com.fikjul.orecurrency.shop.ShopManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Command executor untuk /shop
 */
public class ShopCommand implements CommandExecutor {
    private final OrecoPlugin plugin;
    private final ShopManager shopManager;
    private final ShopGUI shopGUI;

    public ShopCommand(OrecoPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
        this.shopGUI = plugin.getShopGUI();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        // /shop - buka GUI
        if (args.length == 0) {
            shopGUI.openMainMenu(player);
            return true;
        }

        // Admin commands
        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("oreco.shop.admin")) {
                String msg = colorize(plugin.getConfig().getString("messages.no_permission", "&cAnda tidak punya permission!"));
                player.sendMessage(colorize(prefix + msg));
                return true;
            }

            return handleAdminCommand(player, args);
        }

        return true;
    }

    /**
     * Handle admin commands
     */
    private boolean handleAdminCommand(Player player, String[] args) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        if (args.length < 2) {
            player.sendMessage(colorize(prefix + "&cUsage: /shop admin <add|remove|reload|setcategory|list>"));
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "add":
                return handleAddCommand(player, args);
            case "remove":
                return handleRemoveCommand(player, args);
            case "reload":
                return handleReloadCommand(player);
            case "setcategory":
                return handleSetCategoryCommand(player, args);
            case "list":
                return handleListCommand(player);
            default:
                player.sendMessage(colorize(prefix + "&cUnknown subcommand: " + subCommand));
                return true;
        }
    }

    /**
     * /shop admin add <material> <currency1:amount1> [currency2:amount2] ...
     */
    private boolean handleAddCommand(Player player, String[] args) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        if (args.length < 4) {
            player.sendMessage(colorize(prefix + "&cUsage: /shop admin add <material> <currency1:amount1> [currency2:amount2] ..."));
            player.sendMessage(colorize(prefix + "&7Example: /shop admin add DIAMOND_SWORD diamond:2 emerald:10"));
            return true;
        }

        // Parse material
        Material material;
        try {
            material = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(colorize(prefix + "&cMaterial tidak valid: " + args[2]));
            return true;
        }

        // Parse prices
        Map<CurrencyType, Integer> prices = new HashMap<>();
        for (int i = 3; i < args.length; i++) {
            String[] parts = args[i].split(":");
            if (parts.length != 2) {
                player.sendMessage(colorize(prefix + "&cFormat harga tidak valid: " + args[i]));
                player.sendMessage(colorize(prefix + "&7Format: currency:amount (contoh: diamond:2)"));
                return true;
            }

            CurrencyType currency = CurrencyType.fromString(parts[0]);
            if (currency == null) {
                player.sendMessage(colorize(prefix + "&cCurrency tidak valid: " + parts[0]));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(parts[1]);
                if (amount <= 0) {
                    player.sendMessage(colorize(prefix + "&cAmount harus lebih dari 0!"));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(colorize(prefix + "&cAmount harus berupa angka: " + parts[1]));
                return true;
            }

            prices.put(currency, amount);
        }

        // Add item
        if (shopManager.addItem(material, prices, null)) {
            player.sendMessage(colorize(prefix + "&aItem berhasil ditambahkan ke shop!"));
            player.sendMessage(colorize(prefix + "&7Material: &e" + material.name()));
            
            StringBuilder priceStr = new StringBuilder();
            for (Map.Entry<CurrencyType, Integer> entry : prices.entrySet()) {
                if (priceStr.length() > 0) priceStr.append(", ");
                priceStr.append(entry.getValue()).append(" ").append(entry.getKey().getDisplayName());
            }
            player.sendMessage(colorize(prefix + "&7Price: &e" + priceStr));
        } else {
            player.sendMessage(colorize(prefix + "&cGagal menambahkan item!"));
        }

        return true;
    }

    /**
     * /shop admin remove <material>
     */
    private boolean handleRemoveCommand(Player player, String[] args) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        if (args.length < 3) {
            player.sendMessage(colorize(prefix + "&cUsage: /shop admin remove <material>"));
            return true;
        }

        Material material;
        try {
            material = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(colorize(prefix + "&cMaterial tidak valid: " + args[2]));
            return true;
        }

        if (shopManager.removeItem(material)) {
            player.sendMessage(colorize(prefix + "&aItem berhasil dihapus dari shop!"));
        } else {
            player.sendMessage(colorize(prefix + "&cItem tidak ditemukan di shop!"));
        }

        return true;
    }

    /**
     * /shop admin reload
     */
    private boolean handleReloadCommand(Player player) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        shopManager.reloadShops();
        player.sendMessage(colorize(prefix + "&aShop configuration reloaded!"));

        return true;
    }

    /**
     * /shop admin setcategory <material> <category>
     */
    private boolean handleSetCategoryCommand(Player player, String[] args) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        if (args.length < 4) {
            player.sendMessage(colorize(prefix + "&cUsage: /shop admin setcategory <material> <category>"));
            return true;
        }

        Material material;
        try {
            material = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(colorize(prefix + "&cMaterial tidak valid: " + args[2]));
            return true;
        }

        String category = args[3];

        if (shopManager.setCategory(material, category)) {
            player.sendMessage(colorize(prefix + "&aKategori item berhasil diubah!"));
            player.sendMessage(colorize(prefix + "&7Material: &e" + material.name()));
            player.sendMessage(colorize(prefix + "&7Category: &e" + category));
        } else {
            player.sendMessage(colorize(prefix + "&cItem tidak ditemukan di shop!"));
        }

        return true;
    }

    /**
     * /shop admin list
     */
    private boolean handleListCommand(Player player) {
        String prefix = colorize(plugin.getConfig().getString("messages.prefix", "&8[&6Oreco&8] &r"));

        player.sendMessage(colorize(prefix + "&6&l===== Shop Items ====="));

        for (ShopItem item : shopManager.getAllItems()) {
            String line = String.format("&e%s &7- &f%s &7(%s)", 
                item.getMaterial().name(), 
                item.formatPrice(),
                item.getCategory());
            player.sendMessage(colorize(prefix + line));
        }

        player.sendMessage(colorize(prefix + "&6&l====================="));

        return true;
    }

    private String colorize(String text) {
        if (text == null) return "";
        return LegacyComponentSerializer.legacyAmpersand().serialize(
            LegacyComponentSerializer.legacyAmpersand().deserialize(text)
        );
    }
}
