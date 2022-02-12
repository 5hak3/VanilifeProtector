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

public class ToggleWhitelist implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final ConfigLoader cl;

    public ToggleWhitelist(JavaPlugin plugin, ConfigLoader cl) {
        this.cl = cl;
        this.plugin = plugin;
    }

    /**
     * コマンド発行時にホワリスをON/OFFする
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        cl.whitelist.toggleWlist();
        if (cl.whitelist.isEnable) {
            sender.sendMessage(ChatColor.AQUA + "ホワイトリストを有効にしました．");
        }
        else {
            sender.sendMessage(ChatColor.AQUA + "ホワイトリストを無効にしました．");
        }
        plugin.getLogger().info("理由: " + sender.getName() + "によるコマンド操作．");
        return true;
    }

    /**
     * ホワイトリスト無効時にプレイヤーが離脱した際に，そのプレイヤーがホワイトリスト・オブザーバなら，
     * 他にホワイトリスト・オブザーバがいなければホワイトリストをONにする．
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        if (cl.whitelist.isEnable) return;
        if (!event.getPlayer().hasPermission("vanprotect.observer")) return;
        for (Player p: Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(event.getPlayer().getUniqueId())) continue;
            if (p.hasPermission("vanprotect.observer")) return;
        }
        cl.whitelist.toggleWlist();
        plugin.getLogger().info("理由: " + event.getPlayer().getName() + "がログアウトしたため．");
    }

    /**
     * ホワイトリスト有効時にプレイヤーが入場した際に，
     * そのプレイヤーがホワイトリスト・オブザーバならホワイトリストを無効にする．
     * そうでなければ，そのプレイヤーがホワイトリストに入っていなければKickする．
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!(cl.whitelist.isEnable)) return;
        Player player = event.getPlayer();

        if (player.hasPermission("vanprotect.observer")) {
            cl.whitelist.toggleWlist();
            plugin.getLogger().info("理由: " + event.getPlayer().getName() + "がログインしたため．");
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "ホワイトリストを無効にしました．"),
                    20*3);

            return;
        }

        if (cl.whitelist.whitelists.contains(player.getUniqueId())) {
            plugin.getLogger().info("理由: " + event.getPlayer().getName() + "がホワイトリストに含まれているため回避．");
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "ホワイトリストを回避しました．"),
                    20*3);
            return;
        }

        player.kickPlayer("現在ホワイトリストが有効になっております．数時間後に再試行してください．");
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
        if (!event.getAffected().isAfk()) {
            if (cl.whitelist.isEnable) return;
            if (!event.getAffected().getBase().hasPermission("vanprotect.observer")) return;

            Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            assert ess != null;
            for (Player p: Bukkit.getOnlinePlayers()) {
                if (p.getUniqueId().equals(event.getAffected().getBase().getUniqueId())) continue;
                if (p.hasPermission("vanprotect.observer") && !ess.getUser(p).isAfk()) return;
            }

            cl.whitelist.toggleWlist();
            plugin.getLogger().info("理由: " + event.getAffected().getBase().getName() + "がAFKになったため．");
        }
        else if (event.getAffected().isAfk()) {
            if (!cl.whitelist.isEnable) return;
            if (!event.getAffected().getBase().hasPermission("vanprotect.observer")) return;
            cl.whitelist.toggleWlist();
            plugin.getLogger().info("理由: " + event.getAffected().getBase().getName() + "が非AFKになったため．");
        }
    }
}
