package net.azisaba.vanilife.vanilifeprotector;

import net.azisaba.vanilife.vanilifeprotector.whitelisting.AddWhitelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.ToggleWhitelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.ViewWhitelist;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanilifeProtector extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigLoader cl = new ConfigLoader(this);
        Emergency em = new Emergency(this);
        AddWhitelist awl = new AddWhitelist(this, cl);
        ToggleWhitelist twl = new ToggleWhitelist(cl);
        ViewWhitelist vwl = new ViewWhitelist(cl);

        Objects.requireNonNull(getCommand("vpreload")).setExecutor(cl);
        Objects.requireNonNull(getCommand("emergency")).setExecutor(em);
        Objects.requireNonNull(getCommand("whitelist#add")).setExecutor(awl);
        Objects.requireNonNull(getCommand("whitelist#remove")).setExecutor(awl);
        Objects.requireNonNull(getCommand("whitelist#toggle")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#view")).setExecutor(vwl);
        getServer().getPluginManager().registerEvents(twl, this);
        getServer().getPluginManager().registerEvents(awl, this);
    }
}
