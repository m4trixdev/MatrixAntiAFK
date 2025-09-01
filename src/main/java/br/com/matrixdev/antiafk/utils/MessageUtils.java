package br.com.matrixdev.antiafk.utils;

import br.com.matrixdev.antiafk.MatrixAntiAFK;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {

    private final MatrixAntiAFK plugin;

    public MessageUtils(MatrixAntiAFK plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(CommandSender sender, String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        String prefix = plugin.getConfigManager().getPrefix();
        if (prefix == null || prefix.trim().isEmpty()) {
            sender.sendMessage(colorize(message));
        } else {
            sender.sendMessage(colorize(prefix + " " + message));
        }
    }

    public void sendRawMessage(CommandSender sender, String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        sender.sendMessage(colorize(message));
    }

    public String getMessage(String key) {
        String message = plugin.getConfigManager().getMessage(key);
        if (message == null || message.trim().isEmpty()) {
            return "";
        }
        return colorize(message);
    }

    public String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String stripColors(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(message));
    }
}