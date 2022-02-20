package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class KickPlayer implements Listener {
    private final ConfigLoader cl;
    private final Permission vault;

    public KickPlayer(ConfigLoader cl) {
        this.cl = cl;
        RegisteredServiceProvider<Permission> registration = Bukkit.getServicesManager().getRegistration(Permission.class);
        this.vault = Objects.requireNonNull(registration).getProvider();
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (!cl.whitelist.isEnable) return;
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
        if (cl.whitelist.whitelists.contains(event.getUniqueId())) {
            Bukkit.getLogger().info("理由: " + event.getName() + "がホワイトリスト(PL)に含まれているため回避．");
            return;
        }
        if (vault.playerHas(null, player, "vanprotect.observer")) {
            Bukkit.getLogger().info("理由: " + event.getName() + "がオブザーバに含まれているため回避．");
            return;
        }
        if (player.isWhitelisted()) {
            Bukkit.getLogger().info("理由: " + event.getName() + "がホワイトリスト(MC)に含まれているため回避．");
            return;
        }

        event.setKickMessage("現在ホワイトリストが有効になっております．数時間後に再試行してください．");
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
    }
}
