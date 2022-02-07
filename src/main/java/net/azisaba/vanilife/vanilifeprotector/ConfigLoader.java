package net.azisaba.vanilife.vanilifeprotector;

import net.azisaba.vanilife.vanilifeprotector.whitelisting.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ConfigLoader implements CommandExecutor {
    private final JavaPlugin plugin;
    public final Whitelist whitelist;
    // unused
    // public final ConfigProtect protect;

//    public static class ConfigProtect {
//        public boolean isEnable;
//        public ConfigProtect() {
//            isEnable = false;
//        }
//    }

    public ConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.whitelist = new Whitelist(plugin);
//        this.protect = new ConfigProtect();
        if (this.loadConfig()) Bukkit.getServer().getLogger().info("Config Load Success!");
        else Bukkit.getServer().getLogger().warning("Config Load Failed.");
    }

    public boolean loadConfig() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        FileConfiguration fc = this.plugin.getConfig();
        ConfigurationSection cs;
        Object property;

        // whitelist
        if(!(this.whitelist.loadWlistData())) return false;
        if(!(this.whitelist.loadOlistData())) return false;
        cs = fc.getConfigurationSection("whitelist");
        if (cs == null) return false;
        property = cs.getBoolean("isEnable");
        this.whitelist.isEnable = (boolean) property;
        property = cs.getInt("minDays");
        this.whitelist.minDays = (int) property;

        // protect
//        cs = fc.getConfigurationSection("protect");
//        if (cs == null) return false;
//        property = cs.get("isEnable");
//        if (!(property instanceof Boolean)) return false;
//        this.protect.isEnable = (boolean) property;

        return true;
    }

    /**
     * リロードするやつ
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return loadConfig();
    }
}
