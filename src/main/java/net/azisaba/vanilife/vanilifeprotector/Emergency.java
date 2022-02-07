package net.azisaba.vanilife.vanilifeprotector;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Emergency implements CommandExecutor {
    private final JavaPlugin plugin;

    public Emergency(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * きんきゅーてーし
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.setWhitelist(true);
        Set<OfflinePlayer> admins = plugin.getServer().getWhitelistedPlayers();
        ArrayList<UUID> adminsUid = new ArrayList<>();
        for (OfflinePlayer p: admins) adminsUid.add(p.getUniqueId());
        for (Player p: plugin.getServer().getOnlinePlayers()) {
            if (!adminsUid.contains(p.getUniqueId())) p.kickPlayer("しばらくお待ちください．");
        }
        return true;
    }
}
