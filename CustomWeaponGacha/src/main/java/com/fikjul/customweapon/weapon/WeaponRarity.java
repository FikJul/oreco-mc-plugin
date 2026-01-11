package com.fikjul.customweapon.weapon;

/**
 * Enum untuk rarity tiers weapon
 */
public enum WeaponRarity {
    COMMON("&f", "COMMON", 1),
    RARE("&9", "RARE", 2),
    EPIC("&5", "EPIC", 3),
    LEGEND("&6", "LEGEND", 4),
    MYTHIC("&c", "MYTHIC", 5),
    GOD("&d", "GOD", 6);

    private final String color;
    private final String displayName;
    private final int tier;

    WeaponRarity(String color, String displayName, int tier) {
        this.color = color;
        this.displayName = displayName;
        this.tier = tier;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTier() {
        return tier;
    }

    public static WeaponRarity fromString(String str) {
        for (WeaponRarity rarity : values()) {
            if (rarity.name().equalsIgnoreCase(str)) {
                return rarity;
            }
        }
        return COMMON;
    }
}
