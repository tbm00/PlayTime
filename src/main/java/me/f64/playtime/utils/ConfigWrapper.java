package me.f64.playtime.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigWrapper {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private final String folderName;
    private final String fileName;

    public ConfigWrapper(JavaPlugin instance, String folderName, String fileName) {
        this.plugin = instance;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    public void createFile(String s, List<String> h) {
        reloadConfig();
        saveConfig();
        loadConfig(h);
        if (s != null) {
            this.plugin.getLogger().info(s);
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            reloadConfig();
        }
        return this.config;
    }

    public void loadConfig(List<String> h) {
        this.config.options().setHeader(h);
        this.config.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null) {
            if ((this.folderName != null) && (!this.folderName.isEmpty())) {
                this.configFile = new File(this.plugin.getDataFolder() + File.separator + this.folderName,
                        this.fileName);
            } else {
                this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void saveConfig() {
        if ((this.config == null) || (this.configFile == null)) {
            return;
        }
        try {
            getConfig().save(this.configFile);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
        }
    }
}
