package com.fikjul.orecurrency.currency;

import org.bukkit.Material;

/**
 * Enum yang merepresentasikan tipe-tipe mata uang dalam sistem Oreco
 * Setiap currency memiliki material dan nilai dalam copper equivalent
 */
public enum CurrencyType {
    COPPER(Material.COPPER_INGOT, 1L),
    IRON(Material.IRON_INGOT, 64L),
    GOLD(Material.GOLD_INGOT, 6144L),              // 96 * 64
    EMERALD(Material.EMERALD, 393216L),            // 64 * 6144
    DIAMOND(Material.DIAMOND, 100663296L),         // 256 * 393216
    NETHERITE(Material.NETHERITE_INGOT, 25769803776L); // 256 * 100663296

    private final Material material;
    private final long copperValue;

    CurrencyType(Material material, long copperValue) {
        this.material = material;
        this.copperValue = copperValue;
    }

    public Material getMaterial() {
        return material;
    }

    public long getCopperValue() {
        return copperValue;
    }

    /**
     * Mendapatkan CurrencyType dari Material
     */
    public static CurrencyType fromMaterial(Material material) {
        for (CurrencyType type : values()) {
            if (type.material == material) {
                return type;
            }
        }
        return null;
    }

    /**
     * Mendapatkan CurrencyType dari string name
     */
    public static CurrencyType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Mendapatkan nama currency yang user-friendly
     */
    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
