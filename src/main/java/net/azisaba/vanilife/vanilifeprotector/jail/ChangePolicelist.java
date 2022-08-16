package net.azisaba.vanilife.vanilifeprotector.jail;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// 中身は大体ChangeWhitelistと同様
public class ChangePolicelist implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final ConfigLoader cl;

    public ChangePolicelist(JavaPlugin plugin, ConfigLoader cl) {
        this.plugin = plugin;
        this.cl = cl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "対象のMCIDを指定してください．");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "対象が見つかりませんでした．");
            return false;
        }

        boolean res = false;
        if (command.getName().equalsIgnoreCase("policelist#add")) {
            res = add(player);
            if (res) sender.sendMessage(ChatColor.AQUA + "ポリスリスト登録に成功しました．");
            else sender.sendMessage(ChatColor.RED + "ポリスリスト登録に失敗しました．");
        }
        else if (command.getName().equalsIgnoreCase("policelist#remove")) {
            res = remove(player);
            if (res) sender.sendMessage(ChatColor.AQUA + "ポリスリスト登録解除に成功しました．");
            else sender.sendMessage(ChatColor.RED + "ポリスリスト登録解除に失敗しました．");
        }

        return res;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // TODO call add() when player join
        add(event.getPlayer());
    }

    private boolean add(Player player) {
        if (cl.policelist.policelist.contains(player.getUniqueId())) return false;

        int plMinDays = this.cl.policelist.minDays;
        if (plMinDays < 0) return false;

        int totalSec = player.getStatistic(Statistic.TOTAL_WORLD_TIME);
        if (totalSec > plMinDays * 24 * 3600 * 20) {
            cl.policelist.policelist.add(player.getUniqueId());
            cl.policelist.savePlistData();
            Bukkit.getServer().getLogger().info(
                    player.getName() + "(" + player.getUniqueId() + ") was Policelisted!");
            Bukkit.getScheduler().runTaskLater(
                    this.plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "あなたはポリスリストに追加されました！"),
                    20*3);
        }
        return true;
    }

    public boolean remove(Player player) {
        if (!cl.policelist.policelist.contains(player.getUniqueId())) return false;
        cl.policelist.policelist.remove(player.getUniqueId());
        cl.policelist.savePlistData();
        Bukkit.getServer().getLogger().info(
                player.getName() + "(" + player.getUniqueId() + ") was no longer Policelisted!");
        Bukkit.getScheduler().runTaskLater(
                this.plugin,
                () -> player.sendMessage(ChatColor.RED + "あなたはポリスリストから削除されました！"),
                20*3);

        return true;
    }
}
