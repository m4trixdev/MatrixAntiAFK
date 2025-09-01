package br.com.matrixdev.antiafk;

import br.com.matrixdev.antiafk.commands.AntiAFKCommand;
import br.com.matrixdev.antiafk.config.ConfigManager;
import br.com.matrixdev.antiafk.listeners.PlayerActivityListener;
import br.com.matrixdev.antiafk.managers.AFKManager;
import br.com.matrixdev.antiafk.managers.ChallengeManager;
import br.com.matrixdev.antiafk.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class MatrixAntiAFK extends JavaPlugin {

    private static MatrixAntiAFK instance;
    private ConfigManager configManager;
    private AFKManager afkManager;
    private ChallengeManager challengeManager;
    private MessageUtils messageUtils;

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigs();
        this.messageUtils = new MessageUtils(this);
        this.afkManager = new AFKManager(this);
        this.challengeManager = new ChallengeManager(this);
        getServer().getPluginManager().registerEvents(new PlayerActivityListener(this), this);
        getCommand("matrixantiafk").setExecutor(new AntiAFKCommand(this));
        getLogger().info("MatrixAntiAFK v1.0.0 ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (afkManager != null) {
            afkManager.shutdown();
        }
        if (challengeManager != null) {
            challengeManager.cleanup();
        }
        getLogger().info("MatrixAntiAFK desativado!");
    }

    public static MatrixAntiAFK getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AFKManager getAFKManager() {
        return afkManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public MessageUtils getMessageUtils() {
        return messageUtils;
    }
}