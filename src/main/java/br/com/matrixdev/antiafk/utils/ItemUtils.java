package br.com.matrixdev.antiafk.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static ItemStack createItem(Material material, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1, (short) data);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null && !name.trim().isEmpty()) {
                meta.setDisplayName(colorize(name));
            }

            if (lore != null && !lore.isEmpty()) {
                List<String> colorizedLore = new ArrayList<String>();
                for (String line : lore) {
                    if (line != null) {
                        colorizedLore.add(colorize(line));
                    }
                }
                meta.setLore(colorizedLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        return createItem(material, 0, name, lore);
    }

    public static ItemStack createItem(Material material, int data, String name) {
        return createItem(material, data, name, null);
    }

    public static ItemStack createItem(Material material, int data) {
        return createItem(material, data, null, null);
    }

    public static ItemStack createItem(Material material) {
        return createItem(material, 0, null, null);
    }

    public static boolean isValidMaterial(String materialName) {
        if (materialName == null || materialName.trim().isEmpty()) {
            return false;
        }

        try {
            Material.valueOf(materialName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Material getMaterialSafely(String materialName, Material fallback) {
        if (materialName == null || materialName.trim().isEmpty()) {
            return fallback;
        }

        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static String colorize(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) {
            return true;
        }

        if (item1 == null || item2 == null) {
            return false;
        }

        return item1.isSimilar(item2);
    }

    public static ItemStack cloneItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        return item.clone();
    }
}