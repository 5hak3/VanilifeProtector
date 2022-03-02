package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class KickPlayer implements Listener {
    private final ConfigLoader cl;
    private final Permission vault;
    private ArrayList<UUID> kickedPlayers;
    private int kickedCount;

    public KickPlayer(ConfigLoader cl) {
        this.cl = cl;
        RegisteredServiceProvider<Permission> registration = Bukkit.getServicesManager().getRegistration(Permission.class);
        this.vault = Objects.requireNonNull(registration).getProvider();
        this.kickedPlayers = new ArrayList<>();
        this.kickedCount = 0;
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
        this.sendMessage(Bukkit.getOfflinePlayer(event.getUniqueId()));
    }

    private void sendMessage(OfflinePlayer player) {
        if (kickedPlayers.contains(player.getUniqueId())) return;
        kickedPlayers.add(player.getUniqueId());
        kickedCount++;
        ShuttedPlayerNotifier.send(player, kickedCount);
    }

    public void resetKickedCount() {
        this.kickedCount = 0;
        this.kickedPlayers.clear();
    }
}
