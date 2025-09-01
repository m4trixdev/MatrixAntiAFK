package br.com.matrixdev.antiafk.config;

import br.com.matrixdev.antiafk.MatrixAntiAFK;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private final MatrixAntiAFK plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    private int afkTime;
    private int checkInterval;
    private double movementThreshold;
    private int repetitiveLimit;
    private int challengeTimeout;
    private String challengeMenuTitle;
    private int challengeMenuSize;
    private boolean challengeFillItemEnabled;
    private Material challengeFillMaterial;
    private int challengeFillItemData;
    private String challengeFillItemName;
    private List<String> challengeFillItemLore;
    private List<Map<String, Object>> challengeCustomItems;
    private String challengeType;
    private String challengeCorrectItemName;
    private List<String> challengeCorrectItemLore;
    private List<String> challengeMaterials;
    private String challengeSoundSuccess;
    private float challengeSoundSuccessVolume;
    private float challengeSoundSuccessPitch;
    private String challengeSoundFail;
    private float challengeSoundFailVolume;
    private float challengeSoundFailPitch;
    private String punishmentType;
    private String punishmentBanCommand;
    private String punishmentTeleportLocation;
    private String punishmentCustomCommand;
    private String prefix;

    public ConfigManager(MatrixAntiAFK plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        loadConfig();
        loadMessages();
        cacheConfigValues();
    }

    private void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfigs();
        }
        config = plugin.getConfig();
        plugin.reloadConfig();
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void cacheConfigValues() {
        afkTime = config.getInt("afk.time", 300);
        checkInterval = config.getInt("afk.check-interval", 10);
        movementThreshold = config.getDouble("afk.movement-threshold", 2.0);
        repetitiveLimit = config.getInt("afk.repetitive-limit", 10);

        challengeTimeout = config.getInt("challenge.timeout", 30);
        challengeMenuTitle = config.getString("challenge.menu.title", "&cVerificacao Anti-AFK");
        challengeMenuSize = config.getInt("challenge.menu.size", 27);

        challengeFillItemEnabled = config.getBoolean("challenge.menu.fill-item.enabled", true);
        String fillMaterialName = config.getString("challenge.menu.fill-item.material", "STAINED_GLASS_PANE");
        int fillData = config.getInt("challenge.menu.fill-item.data", 7);
        try {
            challengeFillMaterial = Material.valueOf(fillMaterialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Material de preenchimento invalido, usando STONE.");
            challengeFillMaterial = Material.STONE;
        }
        challengeFillItemData = fillData;
        challengeFillItemName = config.getString("challenge.menu.fill-item.name", "&7");
        challengeFillItemLore = config.getStringList("challenge.menu.fill-item.lore");

        challengeType = config.getString("challenge.type", "click");
        challengeCorrectItemName = config.getString("challenge.menu.correct-item.name", "&aClique aqui!");
        challengeCorrectItemLore = config.getStringList("challenge.menu.correct-item.lore");
        challengeMaterials = config.getStringList("challenge.materials");
        challengeCustomItems = new ArrayList<Map<String, Object>>();
        List<Map<?, ?>> items = config.getMapList("challenge.menu.custom-items");
        for (Map<?, ?> m : items) {
            Map<String, Object> entry = new HashMap<String, Object>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                entry.put(e.getKey().toString(), e.getValue());
            }
            challengeCustomItems.add(entry);
        }

        challengeSoundSuccess = config.getString("challenge.sounds.success.name", "ENTITY_PLAYER_LEVELUP");
        challengeSoundSuccessVolume = (float) config.getDouble("challenge.sounds.success.volume", 1.0);
        challengeSoundSuccessPitch = (float) config.getDouble("challenge.sounds.success.pitch", 1.0);

        challengeSoundFail = config.getString("challenge.sounds.fail.name", "BLOCK_ANVIL_LAND");
        challengeSoundFailVolume = (float) config.getDouble("challenge.sounds.fail.volume", 1.0);
        challengeSoundFailPitch = (float) config.getDouble("challenge.sounds.fail.pitch", 1.0);

        punishmentType = config.getString("punishment.type", "kick");
        punishmentBanCommand = config.getString("punishment.ban-command", "ban {player} AFK detectado");
        punishmentTeleportLocation = config.getString("punishment.teleport-location", "");
        punishmentCustomCommand = config.getString("punishment.custom-command", "");
    }

    public void saveDefaultConfigs() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            config = plugin.getConfig();

            config.set("afk.time", 300);
            config.set("afk.check-interval", 10);
            config.set("afk.movement-threshold", 2.0);
            config.set("afk.repetitive-limit", 10);

            config.set("challenge.timeout", 30);
            config.set("challenge.menu.title", "&cVerificacao Anti-AFK");
            config.set("challenge.menu.size", 27);

            config.set("challenge.menu.fill-item.enabled", true);
            config.set("challenge.menu.fill-item.material", "STAINED_GLASS_PANE");
            config.set("challenge.menu.fill-item.data", 7);
            config.set("challenge.menu.fill-item.name", "&7");
            config.set("challenge.menu.fill-item.lore", Arrays.asList());

            config.set("challenge.type", "click");
            config.set("challenge.menu.correct-item.name", "&aClique aqui!");
            config.set("challenge.menu.correct-item.lore", Arrays.asList(
                    "&7Clique neste item para provar",
                    "&7que voce nao e um bot!"
            ));

            config.set("challenge.materials", Arrays.asList(
                    "DIAMOND", "EMERALD", "GOLD_INGOT", "IRON_INGOT", "REDSTONE", "COAL", "APPLE", "BREAD", "ARROW"
            ));

            List<Map<String, Object>> defaultCustomItems = new ArrayList<Map<String, Object>>();
            config.set("challenge.menu.custom-items", defaultCustomItems);

            config.set("challenge.sounds.success.name", "ENTITY_PLAYER_LEVELUP");
            config.set("challenge.sounds.success.volume", 1.0);
            config.set("challenge.sounds.success.pitch", 1.0);

            config.set("challenge.sounds.fail.name", "BLOCK_ANVIL_LAND");
            config.set("challenge.sounds.fail.volume", 1.0);
            config.set("challenge.sounds.fail.pitch", 1.0);

            config.set("punishment.type", "kick");
            config.set("punishment.ban-command", "ban {player} AFK detectado");
            config.set("punishment.teleport-location", "");
            config.set("punishment.custom-command", "");

            plugin.saveConfig();
        }

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.getParentFile().mkdirs();
                messagesFile.createNewFile();

                FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

                messagesConfig.set("prefix", "&7[&cMatrixAFK&7]");
                messagesConfig.set("challenge.warning", "&eVoce foi detectado como AFK. Complete o desafio para continuar!");
                messagesConfig.set("challenge.success", "&aDesafio completado com sucesso!");
                messagesConfig.set("punishment.kick", "&cVoce foi expulso por ficar AFK.");
                messagesConfig.set("reload.success", "&aConfiguracao recarregada com sucesso!");
                messagesConfig.set("reload.error", "&cErro ao recarregar as configuracoes!");
                messagesConfig.set("no-permission", "&cVoce nao tem permissao para usar este comando!");

                messagesConfig.save(messagesFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar messages.yml: " + e.getMessage());
            }
        }
    }

    public void reload() {
        plugin.reloadConfig();
        loadConfigs();
    }

    public int getAfkTime() { return afkTime; }
    public int getCheckInterval() { return checkInterval; }
    public double getMovementThreshold() { return movementThreshold; }
    public int getRepetitiveLimit() { return repetitiveLimit; }
    public int getChallengeTimeout() { return challengeTimeout; }
    public String getChallengeMenuTitle() { return challengeMenuTitle; }
    public int getChallengeMenuSize() { return challengeMenuSize; }
    public boolean isChallengeFillItemEnabled() { return challengeFillItemEnabled; }
    public Material getChallengeFillMaterial() { return challengeFillMaterial; }
    public int getChallengeFillItemData() { return challengeFillItemData; }
    public String getChallengeFillItemName() { return challengeFillItemName; }
    public List<String> getChallengeFillItemLore() { return challengeFillItemLore; }
    public String getChallengeType() { return challengeType; }
    public String getChallengeCorrectItemName() { return challengeCorrectItemName; }
    public List<String> getChallengeCorrectItemLore() { return challengeCorrectItemLore; }
    public List<String> getChallengeMaterials() { return challengeMaterials; }
    public List<Map<String, Object>> getChallengeCustomItems() { return challengeCustomItems; }
    public String getChallengeSoundSuccess() { return challengeSoundSuccess; }
    public float getChallengeSoundSuccessVolume() { return challengeSoundSuccessVolume; }
    public float getChallengeSoundSuccessPitch() { return challengeSoundSuccessPitch; }
    public String getChallengeSoundFail() { return challengeSoundFail; }
    public float getChallengeSoundFailVolume() { return challengeSoundFailVolume; }
    public float getChallengeSoundFailPitch() { return challengeSoundFailPitch; }
    public String getPunishmentType() { return punishmentType; }
    public String getPunishmentBanCommand() { return punishmentBanCommand; }
    public String getPunishmentTeleportLocation() { return punishmentTeleportLocation; }
    public String getPunishmentCustomCommand() { return punishmentCustomCommand; }

    public String getMessage(String key) {
        String value = messages.getString(key, "");
        return value == null ? "" : value;
    }

    public String getPrefix() {
        String value = messages.getString("prefix", "&7[&cMatrixAFK&7]");
        return value == null ? "" : value;
    }
}