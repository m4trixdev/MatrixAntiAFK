package br.com.matrixdev.antiafk.managers;

import br.com.matrixdev.antiafk.MatrixAntiAFK;
import br.com.matrixdev.antiafk.data.PlayerData;
import br.com.matrixdev.antiafk.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKManager {

    private final MatrixAntiAFK plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private BukkitTask detectionTask;

    public AFKManager(MatrixAntiAFK plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<UUID, PlayerData>();
        startDetectionTask();
    }

    private void startDetectionTask() {
        long checkInterval = plugin.getConfigManager().getCheckInterval() * 20L;
        if (checkInterval <= 0) {
            plugin.getLogger().warning("O intervalo de verificacao (check-interval) deve ser maior que 0. Usando 10 segundos.");
            checkInterval = 200L;
        }
        detectionTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("matrixantiafk.bypass")) continue;
                    checkPlayerActivity(player);
                }
            }
        }.runTaskTimerAsynchronously(plugin, checkInterval, checkInterval);
    }

    private void checkPlayerActivity(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData data = playerDataMap.get(playerId);
        if (data == null || data.getLastLocation() == null) {
            data = new PlayerData(player.getLocation(), System.currentTimeMillis());
            playerDataMap.put(playerId, data);
            return;
        }
        if (data.isChallengeActive()) {
            if (System.currentTimeMillis() - data.getLastChallengeTime() >= plugin.getConfigManager().getChallengeTimeout() * 1000L) {
                plugin.getChallengeManager().handleChallengeFailed(player);
            }
            return;
        }
        long afkTime = plugin.getConfigManager().getAfkTime() * 1000L;
        if (System.currentTimeMillis() - data.getLastActivity() >= afkTime) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                public void run() {
                    plugin.getChallengeManager().startChallenge(player);
                }
            });
            return;
        }
        checkRepetitiveActivity(player, data);
    }

    private void checkRepetitiveActivity(Player player, PlayerData data) {
        Location currentLocation = player.getLocation();
        if (LocationUtils.isSimilarLocation(currentLocation, data.getLastLocation(),
                plugin.getConfigManager().getMovementThreshold())) {
            data.incrementRepetitiveCount();
            if (data.getRepetitiveCount() >= plugin.getConfigManager().getRepetitiveLimit()) {
                if (!data.isChallengeActive()) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        public void run() {
                            plugin.getChallengeManager().startChallenge(player);
                        }
                    });
                }
            }
        } else {
            data.resetRepetitiveCount();
            data.setLastLocation(currentLocation);
            data.updateActivity(currentLocation);
        }
    }

    public void updatePlayerActivity(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData data = playerDataMap.get(playerId);
        if (data == null) {
            data = new PlayerData(player.getLocation(), System.currentTimeMillis());
            playerDataMap.put(playerId, data);
        } else {
            data.resetRepetitiveCount();
            data.updateActivity(player.getLocation());
        }
    }

    public void removePlayer(UUID playerId) {
        playerDataMap.remove(playerId);
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public void shutdown() {
        if (detectionTask != null) detectionTask.cancel();
        playerDataMap.clear();
    }

    public void reload() {
        if (detectionTask != null) detectionTask.cancel();
        startDetectionTask();
    }
}