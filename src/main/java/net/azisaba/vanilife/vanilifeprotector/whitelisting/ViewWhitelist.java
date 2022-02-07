package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.UUID;

public class ViewWhitelist implements CommandExecutor {
    private final ConfigLoader cl;
    private static final int uuidPerPage = 5;

    public ViewWhitelist(ConfigLoader cl) {
        this.cl = cl;
    }

    /**
     * コマンドが発行されたら多ページでホワイトリストを表示する．
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int pages = cl.whitelist.uuids.size() / uuidPerPage + 1;
        int page = 0;
        if (args.length > 0) {
            page = Integer.parseInt(args[0]) - 1;
            if (page >= pages) {
                sender.sendMessage(ChatColor.YELLOW + "そのページは存在しないため，1ページ目を表示します．");
                page = 0;
            }
        }

        String msg = "ホワイトリスト (Page " + (page+1) + " / " + pages + ")";
        UUID puid;
        for (int i = 0; i < uuidPerPage; i++) {
            puid = cl.whitelist.uuids.get(i+page*uuidPerPage);
            msg += "\n" + Objects.requireNonNull(Bukkit.getPlayer(puid)).getName() + "(" + puid + ")";
        }
        sender.sendMessage(msg);
        return true;
    }
}
