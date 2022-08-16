package net.azisaba.vanilife.vanilifeprotector;

import net.azisaba.vanilife.vanilifeprotector.jail.ChangePolicelist;
import net.azisaba.vanilife.vanilifeprotector.jail.JailCommand;
import net.azisaba.vanilife.vanilifeprotector.jail.ViewPolicelist;
import net.azisaba.vanilife.vanilifeprotector.whitelisting.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VanilifeProtector extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigLoader cl = new ConfigLoader(this);
        Emergency em = new Emergency(this);
        ChangeWhitelist cwl = new ChangeWhitelist(this, cl);
        ChangePolicelist cpl = new ChangePolicelist(this, cl);
        WhitelistTimer wlt = new WhitelistTimer(cl, this, cl.whitelist);
        ToggleWhitelist twl = new ToggleWhitelist(this, cl, wlt);
        ViewWhitelist vwl = new ViewWhitelist(cl);
        ViewPolicelist vpl = new ViewPolicelist(cl);
        KickPlayer kp = new KickPlayer(cl);
        cl.whitelist.setKp(kp);
        PlayerStatisticsOperator pso = new PlayerStatisticsOperator();
        JailCommand jc = new JailCommand(cl);

        Objects.requireNonNull(getCommand("vpreload")).setExecutor(cl);
        Objects.requireNonNull(getCommand("emergency")).setExecutor(em);
        Objects.requireNonNull(getCommand("whitelist#toggle")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#on")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#off")).setExecutor(twl);
        Objects.requireNonNull(getCommand("whitelist#add")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#remove")).setExecutor(cwl);
        Objects.requireNonNull(getCommand("whitelist#view")).setExecutor(vwl);
        Objects.requireNonNull(getCommand("vpstats#get")).setExecutor(pso);
        Objects.requireNonNull(getCommand("vpstats#set")).setExecutor(pso);
        Objects.requireNonNull(getCommand("whitelist#status")).setExecutor(wlt);
        getServer().getPluginManager().registerEvents(twl, this);
        getServer().getPluginManager().registerEvents(cwl, this);
        getServer().getPluginManager().registerEvents(kp, this);

        Objects.requireNonNull(getCommand("policelist#add")).setExecutor(cpl);
        Objects.requireNonNull(getCommand("policelist#remove")).setExecutor(cpl);
        Objects.requireNonNull(getCommand("policelist#view")).setExecutor(vpl);
        Objects.requireNonNull(getCommand("vjail")).setExecutor(jc);
        getServer().getPluginManager().registerEvents(cpl, this);

        // サーバ起動時にWL開放時刻帯なら無効に、そうでなければ有効にする
        if (wlt.wlTimeTrg) cl.whitelist.offWlist();
        else cl.whitelist.onWlist();
    }
}
