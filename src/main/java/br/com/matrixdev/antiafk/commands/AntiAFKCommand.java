package br.com.matrixdev.antiafk.commands;

import br.com.matrixdev.antiafk.MatrixAntiAFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiAFKCommand implements CommandExecutor, TabCompleter {

    private final MatrixAntiAFK plugin;

    public AntiAFKCommand(MatrixAntiAFK plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "test":
                handleTest(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            case "help":
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("matrixantiafk.admin")) {
            String noPermMsg = plugin.getMessageUtils().getMessage("no-permission");
            if (!noPermMsg.isEmpty()) {
                plugin.getMessageUtils().sendMessage(sender, noPermMsg);
            }
            return;
        }

        try {
            plugin.getConfigManager().reload();
            plugin.getAFKManager().reload();

            String successMsg = plugin.getMessageUtils().getMessage("reload.success");
            if (!successMsg.isEmpty()) {
                plugin.getMessageUtils().sendMessage(sender, successMsg);
            }
        } catch (Exception e) {
            String errorMsg = plugin.getMessageUtils().getMessage("reload.error");
            if (!errorMsg.isEmpty()) {
                plugin.getMessageUtils().sendMessage(sender, errorMsg);
            }
            plugin.getLogger().severe("Erro ao recarregar configurações: " + e.getMessage());
        }
    }

    private void handleTest(CommandSender sender) {
        if (!sender.hasPermission("matrixantiafk.admin")) {
            String noPermMsg = plugin.getMessageUtils().getMessage("no-permission");
            if (!noPermMsg.isEmpty()) {
                plugin.getMessageUtils().sendMessage(sender, noPermMsg);
            }
            return;
        }

        if (!(sender instanceof Player)) {
            plugin.getMessageUtils().sendMessage(sender, "&cApenas jogadores podem usar este comando!");
            return;
        }

        Player player = (Player) sender;
        plugin.getChallengeManager().startChallenge(player);
        plugin.getMessageUtils().sendMessage(sender, "&aDesafio de teste iniciado!");
    }

    private void handleInfo(CommandSender sender) {
        plugin.getMessageUtils().sendMessage(sender, "&7Configuracoes:");
        plugin.getMessageUtils().sendMessage(sender, "&8- &7Tempo AFK: &a" + plugin.getConfigManager().getAfkTime() + "s");
        plugin.getMessageUtils().sendMessage(sender, "&8- &7Intervalo Check: &a" + plugin.getConfigManager().getCheckInterval() + "s");
        plugin.getMessageUtils().sendMessage(sender, "&8- &7Timeout Desafio: &a" + plugin.getConfigManager().getChallengeTimeout() + "s");
        plugin.getMessageUtils().sendMessage(sender, "&8- &7Tipo Punicao: &a" + plugin.getConfigManager().getPunishmentType());
    }

    private void sendHelp(CommandSender sender) {
        plugin.getMessageUtils().sendMessage(sender, "&a/matrixantiafk help &7- &fMostra esta ajuda");
        plugin.getMessageUtils().sendMessage(sender, "&a/matrixantiafk info &7- &fInformacoes do plugin");
        if (sender.hasPermission("matrixantiafk.admin")) {
            plugin.getMessageUtils().sendMessage(sender, "&a/matrixantiafk reload &7- &fRecarrega configuracoes");
            plugin.getMessageUtils().sendMessage(sender, "&a/matrixantiafk test &7- &fTesta o sistema de desafio");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "info", "reload", "test");
            String arg = args[0].toLowerCase();

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(arg)) {
                    if (subCommand.equals("reload") || subCommand.equals("test")) {
                        if (sender.hasPermission("matrixantiafk.admin")) {
                            completions.add(subCommand);
                        }
                    } else {
                        completions.add(subCommand);
                    }
                }
            }
        }
        return completions;
    }
}