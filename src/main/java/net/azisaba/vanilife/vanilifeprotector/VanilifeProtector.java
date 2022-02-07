package net.azisaba.vanilife.vanilifeprotector;

//import net.azisaba.vanilife.vanilifeprotector.whitelisting.ChangeObserverlist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.ChangeWhitelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.ToggleWhitelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.ViewWhitelist;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanilifeProtector extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigLoader cl = new ConfigLoader(this);
        Emergency em = new Emergency(this);
        ChangeWhitelist cwl = new ChangeWhitelist(this, cl);
//        ChangeObserverlist col = new ChangeObserverlist(this, cl);
        ToggleWhitelist twl = new ToggleWhitelist(this, cl);
        ViewWhitelist vwl = new ViewWhitelist(cl);

        Objects.requireNonNull(getCommand("vpreload")).setExecutor(cl);
        Objects.requireNonNull(getCommand("emergency")).setExecutor(em);
        Objects.requireNonNull(getCommand("whitelist#toggle")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#add")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#remove")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#view")).setExecutor(vwl);
//        Objects.requireNonNull(getCommand("observer#add")).setExecutor(col);
//        Objects.requireNonNull(getCommand("observer#remove")).setExecutor(col);
//        Objects.requireNonNull(getCommand("observer#view")).setExecutor(vwl);
        getServer().getPluginManager().registerEvents(twl, this);
        getServer().getPluginManager().registerEvents(cwl, this);
    }
}
