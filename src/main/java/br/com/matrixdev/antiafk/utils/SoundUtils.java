package br.com.matrixdev.antiafk.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void play(Player player, String soundName, float volume, float pitch) {
        if (soundName == null || soundName.trim().isEmpty()) return;
        try {
            Sound sound;
            try {
                sound = Sound.valueOf(soundName);
            } catch (IllegalArgumentException e) {
                sound = getLegacySound(soundName);
            }
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception ignored) {
        }
    }

    private static Sound getLegacySound(String name) {
        String legacy = name;
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            if (name.equalsIgnoreCase("BLOCK_NOTE_BLOCK_BELL")) return Sound.valueOf("NOTE_PLING");
            if (name.equalsIgnoreCase("ENTITY_PLAYER_LEVELUP")) return Sound.valueOf("LEVEL_UP");
            if (name.equalsIgnoreCase("BLOCK_ANVIL_LAND")) return Sound.valueOf("ANVIL_LAND");
            if (name.equalsIgnoreCase("ENTITY_VILLAGER_NO")) return Sound.valueOf("VILLAGER_NO");
            if (name.equalsIgnoreCase("ENTITY_VILLAGER_YES")) return Sound.valueOf("VILLAGER_YES");
        }
        return Sound.valueOf(legacy);
    }
}