package com.fikjul.customweapon.collection;

import com.fikjul.customweapon.CustomWeaponPlugin;
import org.bukkit.entity.Player;

/**
 * GUI untuk collection (simplified stub)
 */
public class CollectionGUI {
    
    private final CustomWeaponPlugin plugin;

    public CollectionGUI(CustomWeaponPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open collection GUI for player
     */
    public void openCollectionGUI(Player player) {
        // TODO: Implement collection GUI with inventory
        // Show collected weapons, stats, etc.
        player.sendMessage("§7Collection GUI will be implemented in future version.");
        player.sendMessage("§7Your collection: " + plugin.getCollectionManager().getCollectionCount(player) + 
                "/" + plugin.getCollectionManager().getTotalWeapons());
    }
}
