package net.azisaba.vanilife.vanilifeprotector;

import net.azisaba.vanilife.vanilifeprotector.jail.Policelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigLoader implements CommandExecutor {
    private final JavaPlugin plugin;
    public final Whitelist whitelist;
    public final Policelist policelist;
    public int wlOpenTime;
    public int wlCloseTime;

    public ConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.whitelist = new Whitelist(plugin);
        this.policelist = new Policelist(plugin);
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
        cs = fc.getConfigurationSection("whitelist");
        if (cs == null) return false;
        property = cs.getBoolean("isEnable");
        this.whitelist.isEnable = (boolean) property;
        property = cs.getInt("minDays");
        this.whitelist.minDays = (int) property;
        property = cs.getInt("openTime");
        this.wlOpenTime = (int) property;
        property = cs.getInt("closeTime");
        this.wlCloseTime = (int) property;

        // policelist
        if(!(this.policelist.loadPlistData())) return false;
        cs = fc.getConfigurationSection("policelist");
        if (cs == null) return false;
        property = cs.getInt("minDays");
        this.policelist.minDays = (int) property;

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
