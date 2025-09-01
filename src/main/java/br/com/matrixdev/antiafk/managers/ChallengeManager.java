package br.com.matrixdev.antiafk.managers;

import br.com.matrixdev.antiafk.MatrixAntiAFK;
import br.com.matrixdev.antiafk.data.ChallengeData;
import br.com.matrixdev.antiafk.data.PlayerData;
import br.com.matrixdev.antiafk.utils.ItemUtils;
import br.com.matrixdev.antiafk.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

public class ChallengeManager {

    private final MatrixAntiAFK plugin;
    private final Map<UUID, ChallengeData> activeChallenges;
    private final Random random;

    public ChallengeManager(MatrixAntiAFK plugin) {
        this.plugin = plugin;
        this.activeChallenges = new ConcurrentHashMap<UUID, ChallengeData>();
        this.random = new Random();
    }

    public void startChallenge(Player player) {
        UUID playerId = player.getUniqueId();
        if (activeChallenges.containsKey(playerId)) return;
        PlayerData playerData = plugin.getAFKManager().getPlayerData(playerId);
        if (playerData != null) playerData.setChallengeActive(true);
        int correctSlot = random.nextInt(plugin.getConfigManager().getChallengeMenuSize());
        Material correctMaterial = getRandomMaterial();
        ChallengeData challenge = new ChallengeData(correctSlot, correctMaterial);
        activeChallenges.put(playerId, challenge);
        createChallengeMenu(player, challenge);
        startChallengeTimeout(player, challenge);
        String warningMessage = plugin.getMessageUtils().getMessage("challenge.warning");
        if (!warningMessage.isEmpty()) plugin.getMessageUtils().sendMessage(player, warningMessage);
    }

    private void createChallengeMenu(Player player, ChallengeData challenge) {
        String menuTitle = plugin.getConfigManager().getChallengeMenuTitle();
        int menuSize = plugin.getConfigManager().getChallengeMenuSize();
        Inventory menu = Bukkit.createInventory(player, menuSize, menuTitle);
        if (plugin.getConfigManager().isChallengeFillItemEnabled()) {
            ItemStack fillItem = ItemUtils.createItem(
                    plugin.getConfigManager().getChallengeFillMaterial(),
                    plugin.getConfigManager().getChallengeFillItemData(),
                    plugin.getConfigManager().getChallengeFillItemName(),
                    plugin.getConfigManager().getChallengeFillItemLore()
            );
            for (int i = 0; i < menuSize; i++) {
                menu.setItem(i, fillItem);
            }
        }
        List<Map<String, Object>> customItems = plugin.getConfigManager().getChallengeCustomItems();
        if (customItems != null && !customItems.isEmpty()) {
            for (Map<String, Object> item : customItems) {
                if (item == null) continue;
                int slot = ((Number) item.getOrDefault("slot", 0)).intValue();
                String matName = (String) item.getOrDefault("material", "STONE");
                String name = (String) item.getOrDefault("name", "");
                List<String> lore = (List<String>) item.getOrDefault("lore", Arrays.asList());
                int data = 0;
                if (item.containsKey("data")) data = ((Number) item.get("data")).intValue();
                try {
                    Material mat = Material.valueOf(matName.toUpperCase());
                    ItemStack stack = ItemUtils.createItem(mat, data, name, lore);
                    menu.setItem(slot, stack);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Material invalido em custom-items: " + matName);
                }
            }
        }
        ItemStack correctItem = ItemUtils.createItem(
                challenge.getCorrectMaterial(),
                0,
                plugin.getConfigManager().getChallengeCorrectItemName(),
                plugin.getConfigManager().getChallengeCorrectItemLore()
        );
        menu.setItem(challenge.getCorrectSlot(), correctItem);
        player.openInventory(menu);
    }

    private void playResponseSound(Player player, boolean success) {
        String soundName;
        float volume;
        float pitch;
        if (success) {
            soundName = plugin.getConfigManager().getChallengeSoundSuccess();
            volume = plugin.getConfigManager().getChallengeSoundSuccessVolume();
            pitch = plugin.getConfigManager().getChallengeSoundSuccessPitch();
        } else {
            soundName = plugin.getConfigManager().getChallengeSoundFail();
            volume = plugin.getConfigManager().getChallengeSoundFailVolume();
            pitch = plugin.getConfigManager().getChallengeSoundFailPitch();
        }
        SoundUtils.play(player, soundName, volume, pitch);
    }

    private void startChallengeTimeout(Player player, ChallengeData challenge) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activeChallenges.containsKey(player.getUniqueId()) &&
                        activeChallenges.get(player.getUniqueId()).equals(challenge)) {
                    handleChallengeFailed(player);
                }
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getChallengeTimeout() * 20L);
    }

    public boolean handleChallengeResponse(Player player, int clickedSlot) {
        UUID playerId = player.getUniqueId();
        ChallengeData challenge = activeChallenges.get(playerId);
        if (challenge == null) return false;
        if (clickedSlot == challenge.getCorrectSlot()) {
            handleChallengeSuccess(player);
            return true;
        } else {
            handleChallengeFailed(player);
            return true;
        }
    }

    public void handleChallengeFailed(Player player) {
        UUID playerId = player.getUniqueId();
        if (!activeChallenges.containsKey(playerId)) return;
        activeChallenges.remove(playerId);
        PlayerData playerData = plugin.getAFKManager().getPlayerData(playerId);
        if (playerData != null) playerData.setChallengeActive(false);
        player.closeInventory();
        playResponseSound(player, false);
        executePunishment(player);
    }

    private void handleChallengeSuccess(Player player) {
        UUID playerId = player.getUniqueId();
        if (!activeChallenges.containsKey(playerId)) return;
        activeChallenges.remove(playerId);
        PlayerData playerData = plugin.getAFKManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.setChallengeActive(false);
            playerData.updateActivity(player.getLocation());
        }
        player.closeInventory();
        playResponseSound(player, true);
        String successMessage = plugin.getMessageUtils().getMessage("challenge.success");
        if (!successMessage.isEmpty()) plugin.getMessageUtils().sendMessage(player, successMessage);
    }

    private void executePunishment(Player player) {
        String punishmentType = plugin.getConfigManager().getPunishmentType();
        if ("kick".equalsIgnoreCase(punishmentType)) {
            String kickMessage = plugin.getMessageUtils().getMessage("punishment.kick");
            player.kickPlayer(kickMessage.isEmpty() ? "AFK detectado" : kickMessage);
        } else if ("ban".equalsIgnoreCase(punishmentType)) {
            String banCommand = plugin.getConfigManager().getPunishmentBanCommand().replace("{player}", player.getName());
            if (!banCommand.isEmpty()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommand);
        } else if ("teleport".equalsIgnoreCase(punishmentType)) {
            String teleportLocation = plugin.getConfigManager().getPunishmentTeleportLocation();
            if (!teleportLocation.isEmpty()) {
                String[] coords = teleportLocation.split(",");
                if (coords.length >= 3) {
                    try {
                        double x = Double.parseDouble(coords[0]);
                        double y = Double.parseDouble(coords[1]);
                        double z = Double.parseDouble(coords[2]);
                        player.teleport(new org.bukkit.Location(player.getWorld(), x, y, z));
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Formato invalido para teleport: " + teleportLocation);
                    }
                }
            }
        } else if ("command".equalsIgnoreCase(punishmentType)) {
            String customCommand = plugin.getConfigManager().getPunishmentCustomCommand().replace("{player}", player.getName());
            if (!customCommand.isEmpty()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), customCommand);
        }
    }

    private Material getRandomMaterial() {
        List<String> materialsList = plugin.getConfigManager().getChallengeMaterials();
        if (materialsList != null && !materialsList.isEmpty()) {
            String matName = materialsList.get(random.nextInt(materialsList.size()));
            try {
                return Material.valueOf(matName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Material invalido em challenge.materials: " + matName + ". Usando padrao.");
            }
        }
        Material[] defaultMaterials = {
                Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
                Material.IRON_INGOT, Material.REDSTONE, Material.COAL,
                Material.APPLE, Material.BREAD, Material.ARROW
        };
        return defaultMaterials[random.nextInt(defaultMaterials.length)];
    }

    public boolean hasActiveChallenge(UUID playerId) {
        return activeChallenges.containsKey(playerId);
    }

    public void removeChallenge(UUID playerId) {
        activeChallenges.remove(playerId);
    }

    public void cleanup() {
        activeChallenges.clear();
    }
}