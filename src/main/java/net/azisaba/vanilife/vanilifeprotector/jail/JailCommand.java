package net.azisaba.vanilife.vanilifeprotector.jail;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// Depend Essentials
public class JailCommand implements CommandExecutor {
    private final ConfigLoader cl;

    public JailCommand(ConfigLoader cl) {
        this.cl = cl;
    }

    // EssentialsのJailをコンソールからDispatchする
    // （自前実装がめんどくさかったため）
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("vjail")) return true;

        Player player, target;
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().info("vjailはプレイヤー専用コマンドです。");
            return true;
        }
        player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage("vjailにはJail対象ユーザとJail理由を指定してください。");
            return false;
        }
        if ((target = Bukkit.getPlayer(args[0])) == null) {
            player.sendMessage("指定されたユーザが見つかりません。vjailはオフラインプレイヤーを対象にできません。");
            return false;
        }

        // 実行者がPolicelistに入っていることを確認する
        if (!this.cl.policelist.policelist.contains(player.getUniqueId())) {
            player.sendMessage("あなたには実行権限がありません。");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jail " + target.getName());
        VanilifeJailNotifier.send(player, target, args[1]);

        return true;
    }
}
