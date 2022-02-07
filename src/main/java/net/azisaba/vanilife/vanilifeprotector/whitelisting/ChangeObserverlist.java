package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangeObserverlist implements CommandExecutor {
    private final JavaPlugin plugin;
    private final ConfigLoader cl;

    public ChangeObserverlist(JavaPlugin plugin, ConfigLoader cl) {
        this.plugin = plugin;
        this.cl = cl;
    }

    /**
     * コマンドが発行されたら
     * addならMCIDで指定したプレイヤーをオブザーバに入れるか判定し成否を表示する．
     * removeならMCIDで指定したプレイヤーをオブザーバから削除して成否を表示する．
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
        if (command.getName().equalsIgnoreCase("observer#add")) {
            if (res = add(player)) sender.sendMessage(ChatColor.AQUA + "オブザーバ登録に成功しました．");
            else sender.sendMessage(ChatColor.RED + "オブザーバ登録に失敗しました．");
        }
        else if (command.getName().equalsIgnoreCase("observer#remove")) {
            if (res = remove(player)) sender.sendMessage(ChatColor.AQUA + "オブザーバ登録解除に成功しました．");
            else sender.sendMessage(ChatColor.RED + "オブザーバ登録解除に失敗しました．");
        }

        return res;
    }

    /**
     * 指定プレイヤーをオブザーバに入れる
     * @param player 指定プレイヤーのPlayerインスタンス
     * @return 結果の成否
     */
    private boolean add(Player player) {
        if (cl.whitelist.observers.contains(player.getUniqueId())) return false;

        cl.whitelist.observers.add(player.getUniqueId());
        cl.whitelist.saveOlistData();
        Bukkit.getServer().getLogger().info(
                player.getName() + "(" + player.getUniqueId() + ") was Observer!");
        Bukkit.getScheduler().runTaskLater(
                this.plugin,
                () -> player.sendMessage(ChatColor.AQUA + "あなたはオブザーバに追加されました！"),
                20*3);
        return true;
    }

    /**
     * 指定プレイヤーをオブザーバから削除する
     * @param player 指定プレイヤーのPlayerインスタンス
     * @return 結果の成否
     */
    public boolean remove(Player player) {
        if (!cl.whitelist.observers.contains(player.getUniqueId())) return false;
        cl.whitelist.observers.remove(player.getUniqueId());
        cl.whitelist.saveOlistData();
        Bukkit.getServer().getLogger().info(
                player.getName() + "(" + player.getUniqueId() + ") was no longer Observer!");
        Bukkit.getScheduler().runTaskLater(
                this.plugin,
                () -> player.sendMessage(ChatColor.RED + "あなたはオブザーバから削除されました！"),
                20*3);

        return true;
    }
}
