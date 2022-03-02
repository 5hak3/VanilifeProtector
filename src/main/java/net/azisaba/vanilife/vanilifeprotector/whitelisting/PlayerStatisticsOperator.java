package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PlayerStatisticsOperator implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("vpstats#get")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "取得対象1名を指定してください．");
                return false;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p.getName() == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーを取得できませんでした．");
                return false;
            }
            int res = getStats(p);
            int days = res / (24 * 3600 * 20);
            int h = (res - days * 24 * 3600 * 20) / (3600 * 20);
            int m = (res - (days * 24 + h) * 3600 * 20) / (60 * 20);
            int s = (res - ((days * 24 + h) * 60 + m) * 60 * 20) / 20;
            sender.sendMessage("MCID: " + p.getName() + "\nStatTotalWorldTime: " + res +
                    "\n(Days-H-M-S: " + String.format("%d-%02d-%02d-%02d", days, h, m, s) + ")");
        }
        else if (command.getName().equalsIgnoreCase("vpstats#set")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "取得対象1名と設定値を指定してください．");
                return false;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p.getName() == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーを取得できませんでした．");
                return false;
            }
            int v = Integer.parseInt(args[1]);
            if (v <= 0) {
                sender.sendMessage(ChatColor.RED + "設定値が無効です．");
                return false;
            }
            setStats(p, v);
            sender.sendMessage(p.getName() + "に" + getStats(p) + "を設定しました．");
        }
        return true;
    }

    private int getStats(OfflinePlayer player) {
        return player.getStatistic(Statistic.TOTAL_WORLD_TIME);
    }

    private void setStats(OfflinePlayer player, int value) {
        player.setStatistic(Statistic.TOTAL_WORLD_TIME, value);
    }
}
