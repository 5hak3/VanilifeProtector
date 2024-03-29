package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import com.earth2me.essentials.Essentials;
import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ToggleWhitelist implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final ConfigLoader cl;
    private final WhitelistTimer wlt;

    public ToggleWhitelist(JavaPlugin plugin, ConfigLoader cl, WhitelistTimer wlt) {
        this.cl = cl;
        this.plugin = plugin;
        this.wlt = wlt;
    }

    /**
     * コマンド発行時にホワリスをON/OFFする
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("whitelist#toggle")) cl.whitelist.toggleWlist();
        else if (command.getName().equalsIgnoreCase("whitelist#on")) cl.whitelist.onWlist();
        else if (command.getName().equalsIgnoreCase("whitelist#off")) cl.whitelist.offWlist();

        if (cl.whitelist.isEnable) {
            sender.sendMessage(ChatColor.AQUA + "ホワイトリストを有効にしました．");
        } else {
            sender.sendMessage(ChatColor.AQUA + "ホワイトリストを無効にしました．");
        }
        plugin.getLogger().info("理由: " + sender.getName() + "によるコマンド操作．");
        return true;
    }

    /**
     * ホワイトリスト無効時にプレイヤーが離脱した際に，そのプレイヤーがホワイトリスト・オブザーバなら，
     * 他にホワイトリスト・オブザーバがいなければホワイトリストをONにする．
     * ただし、wltのwlTimeTrgがTrueの場合は何もしない
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        if (cl.whitelist.isEnable) return;
        if (wlt.wlTimeTrg) return;
        if (!event.getPlayer().hasPermission("vanprotect.observer")) return;

        Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        assert ess != null;
        for (Player p: Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(event.getPlayer().getUniqueId())) continue;
            if (p.hasPermission("vanprotect.observer") && !ess.getUser(p).isAfk()) return;
        }
        cl.whitelist.onWlist();
        plugin.getLogger().info("理由: " + event.getPlayer().getName() + "がログアウトしたため．");
    }

    /**
     * ホワイトリスト有効時にプレイヤーが入場した際に，
     * そのプレイヤーがホワイトリスト・オブザーバならホワイトリストを無効にする．
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!(cl.whitelist.isEnable)) return;
        Player player = event.getPlayer();

        if (player.hasPermission("vanprotect.observer")) {
            cl.whitelist.offWlist();
            plugin.getLogger().info("理由: " + event.getPlayer().getName() + "がログインしたため．");
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "ホワイトリストを無効にしました．"),
                    20*3);
        }
    }

    /**
     * ホワイトリスト・オブザーバがAfkに移行した際（要: Admin）に，
     * 他のオンラインなホワイトリスト・オブザーバが1人でも非AFKでなければ，
     * ホワイトリストを有効にする．
     * Afkが解除された際に，ホワイトリストを無効にする．
     * → DisconnectするとAFK解除判定が同時に発生し，ホワリスが解除されてしまうため削除
     * @param event ess3のAfkStatusChangeEvent
     */
    @EventHandler
    public void onAFK(AfkStatusChangeEvent event) {
    }
}
