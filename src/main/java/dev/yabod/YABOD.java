package dev.yabod;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.BanList.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

class Listeners implements Listener {
    YABOD yabod;

    public Listeners(YABOD yabod) {
        this.yabod = yabod;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        int banDuration = this.yabod.config.getInt("config.banDurationInSeconds", 60);
        Date date = new Date();
        date.setTime(date.getTime() + (banDuration * 1000));
        Bukkit.getBanList(Type.NAME).addBan(p.getName(), "Don't worry, death only lasts for " + banDuration + "s!", date, null);
        p.kickPlayer("Don't worry, death only lasts for " + banDuration + "s!");
    }
}

public class YABOD extends JavaPlugin {
    File configFile;
    YamlConfiguration config;

    void setupConfig() {
        try {
            this.configFile = new File(this.getDataFolder(), "config.yml");
            if (!this.configFile.exists()) {
                if (this.configFile.getParentFile().mkdirs()) {
                    this.getLogger().info("Created CxTokens folder");
                }
                if (this.configFile.createNewFile()) {
                    this.getLogger().info("Created config file");
                } 
            }
            this.config = YamlConfiguration.loadConfiguration(this.configFile);

            // Fill Config
            if (!this.config.isSet("config.banDurationInSeconds")) {
                this.config.set("config.banDurationInSeconds", 60);
            }
            this.config.save(this.configFile);
        } catch (IOException ex) {
            this.getLogger().warning(ex.getMessage());
        }
    }
    
    @Override
    public void onEnable() {
        setupConfig();
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }
}
