package net.azisaba.vanilife.vanilifeprotector;

import net.azisaba.vanilife.vanilifeprotector.whitelisting.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanilifeProtector extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigLoader cl = new ConfigLoader(this);
        Emergency em = new Emergency(this);
        ChangeWhitelist cwl = new ChangeWhitelist(this, cl);
        ToggleWhitelist twl = new ToggleWhitelist(this, cl);
        ViewWhitelist vwl = new ViewWhitelist(cl);
        KickPlayer kp = new KickPlayer(cl);
        cl.whitelist.setKp(kp);
        PlayerStatisticsOperator pso = new PlayerStatisticsOperator();

        Objects.requireNonNull(getCommand("vpreload")).setExecutor(cl);
        Objects.requireNonNull(getCommand("emergency")).setExecutor(em);
        Objects.requireNonNull(getCommand("whitelist#toggle")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#add")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#remove")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#view")).setExecutor(vwl);
        Objects.requireNonNull(getCommand("vpstats#get")).setExecutor(pso);
        Objects.requireNonNull(getCommand("vpstats#set")).setExecutor(pso);
        getServer().getPluginManager().registerEvents(twl, this);
        getServer().getPluginManager().registerEvents(cwl, this);
        getServer().getPluginManager().registerEvents(kp, this);

        // サーバ起動時にホワリスが無効になっていたら有効にする
        if (!cl.whitelist.isEnable) cl.whitelist.toggleWlist();
    }
}
