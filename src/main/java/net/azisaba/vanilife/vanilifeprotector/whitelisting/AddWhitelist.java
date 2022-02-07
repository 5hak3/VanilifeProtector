package net.azisaba.vanilife.vanilifeprotector.whitelisting;

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

public class AddWhitelist implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final ConfigLoader cl;

    public AddWhitelist(JavaPlugin plugin, ConfigLoader cl) {
        this.plugin = plugin;
        this.cl = cl;
    }

    /**
     * コマンドが発行されたら
     * addならMCIDで指定したプレイヤーをホワリスに入れるか判定し成否を表示する．
     * removeならMCIDで指定したプレイヤーをホワリスから削除して成否を表示する．
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        if (command.getName().equalsIgnoreCase("whitelist#add")) {
            if (res = add(player)) sender.sendMessage(ChatColor.RED + "ホワイトリスト登録に成功しました．");
            else sender.sendMessage(ChatColor.AQUA + "ホワイトリスト登録に失敗しました．");
        }
        else if (command.getName().equalsIgnoreCase("whitelist#remove")) {
            if (res = remove(player)) sender.sendMessage(ChatColor.RED + "ホワイトリスト登録解除に成功しました．");
            else sender.sendMessage(ChatColor.AQUA + "ホワイトリスト登録解除に失敗しました．");
        }

        return res;
    }

    /**
     * プレイヤーが参加したときにMCIDで指定したプレイヤーをホワリスに入れるか判定する．
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // TODO call add() when player join
        add(event.getPlayer());
    }

    /**
     * 指定プレイヤーをホワイトリストに入れる
     * @param player 指定プレイヤーのPlayerインスタンス
     * @return 結果の成否
     */
    private boolean add(Player player) {
        if (cl.whitelist.uuids.contains(player.getUniqueId())) return false;

        int wlMinDays = this.cl.whitelist.minDays;
        if (wlMinDays < 0) return false;

        // TOTAL_WORLD_TIMEはそのワールドに参加している合計Tick
        int totalSec = player.getStatistic(Statistic.TOTAL_WORLD_TIME);
        if (totalSec > wlMinDays * 24 * 3600 * 20) {
            cl.whitelist.uuids.add(player.getUniqueId());
            cl.whitelist.saveWlistData();
            Bukkit.getServer().getLogger().info(
                    player.getName() + "(" + player.getUniqueId() + ") was Whitelisted!");
            Bukkit.getScheduler().runTaskLater(
                    this.plugin,
                    () -> player.sendMessage(ChatColor.AQUA + "あなたはホワイトリストに追加されました！"),
                    20*3);
        }
        return true;
    }

    /**
     * 指定プレイヤーをホワイトリストから削除する
     * @param player 指定プレイヤーのPlayerインスタンス
     * @return 結果の成否
     */
    public boolean remove(Player player) {
        if (!cl.whitelist.uuids.contains(player.getUniqueId())) return false;
        cl.whitelist.uuids.remove(player.getUniqueId());
        cl.whitelist.saveWlistData();
        Bukkit.getServer().getLogger().info(
                player.getName() + "(" + player.getUniqueId() + ") was no longer Whitelisted!");
        Bukkit.getScheduler().runTaskLater(
                this.plugin,
                () -> player.sendMessage(ChatColor.AQUA + "あなたはホワイトリストから削除されました！"),
                20*3);

        return true;
    }
}
