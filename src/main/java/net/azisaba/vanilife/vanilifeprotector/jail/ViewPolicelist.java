package net.azisaba.vanilife.vanilifeprotector.jail;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.azisaba.vanilife.vanilifeprotector.whitelisting.ViewWhitelist.view;

public class ViewPolicelist implements CommandExecutor {
    private final ConfigLoader cl;
    private static final int uuidPerPage = 5;

    public ViewPolicelist(ConfigLoader cl) { this.cl=cl; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int page = 0;
        if (args.length > 0) {
            page = Integer.parseInt(args[0]) - 1;
        }

        if (command.getName().equalsIgnoreCase("policelist#view"))
            view(sender, cl.policelist.policelist, page);
        else return false;

        return true;
    }
}
