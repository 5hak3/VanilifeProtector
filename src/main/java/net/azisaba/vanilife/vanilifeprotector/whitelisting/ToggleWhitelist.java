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
        return true;
    }

    /**
     * ホワイトリスト無効時にプレイヤーが離脱した際に，そのプレイヤーがホワイトリスト・オブザーバなら，
     * 他にホワイトリスト・オブザーバがいなければホワイトリストをONにする．
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (cl.whitelist.isEnable) return;
        if (!cl.whitelist.observers.contains(event.getPlayer().getUniqueId())) return;
        for (Player p: Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(event.getPlayer().getUniqueId())) continue;
            if (cl.whitelist.observers.contains(p.getUniqueId())) return;
        }
        cl.whitelist.toggleWlist();
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

        if (cl.whitelist.observers.contains(player.getUniqueId())) {
            cl.whitelist.toggleWlist();
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "ホワイトリストを無効にしました．"),
                    20*3);

            return;
        }

        if (cl.whitelist.whitelists.contains(player.getUniqueId())) {
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
     * @param event ess3のAfkStatusChangeEvent
     */
    @EventHandler
    public void onAFK(AfkStatusChangeEvent event) {
        if (!event.getAffected().isAfk()) {
            if (cl.whitelist.isEnable) return;
            if (!(cl.whitelist.observers.contains(event.getAffected().getBase().getUniqueId()))) return;

            Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
            assert ess != null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (cl.whitelist.observers.contains(p.getUniqueId())) {
                    if (!ess.getUser(p).isAfk() &&
                        !p.getUniqueId().equals(event.getAffected().getBase().getUniqueId())) return;
                }
            }

            cl.whitelist.toggleWlist();
        }
        else if (event.getAffected().isAfk()) {
            if (!cl.whitelist.isEnable) return;
            if (!(cl.whitelist.observers.contains(event.getAffected().getBase().getUniqueId()))) return;
            cl.whitelist.toggleWlist();
        }
    }
}
