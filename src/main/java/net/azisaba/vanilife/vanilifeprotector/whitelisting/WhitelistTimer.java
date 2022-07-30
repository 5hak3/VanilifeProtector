package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import net.azisaba.vanilife.vanilifeprotector.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 前提として1日1回は再起動されることとしている
// そうでない場合は、実行されたタイミングで再スケジューリングが必要
public class WhitelistTimer implements CommandExecutor {
    public boolean wlTimeTrg;
    private final ConfigLoader cl;
    private final Plugin pl;
    private final Whitelist wl;

    public WhitelistTimer(ConfigLoader cl, Plugin pl, Whitelist wl) {
        this.wlTimeTrg = false;
        this.cl = cl;
        this.pl = pl;
        this.wl = wl;

        runTimer();
    }

    public void runTimer() {
        LocalDateTime now = LocalDateTime.now();

        // サーバ起動時刻（1日の始まり）
        LocalDateTime start = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                5,
                0
        );
        if (now.isBefore(start)) start = start.minusDays(1);

        BukkitScheduler scheduler = Bukkit.getScheduler();

        // WL開放時刻のスケジューリング
        LocalDateTime openTime = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                cl.wlOpenTime,
                0
        );
        if (openTime.isBefore(start))
            openTime = openTime.plusDays(1L);

        scheduler.scheduleSyncDelayedTask(pl, new Runnable() {
                @Override
                public void run() {
                    wlTimeTrg = true;
                    if (wl.isEnable) {
                        wl.toggleWlist();
                    }
                }
            }, Duration.between(now, openTime).toSeconds());

        // WL閉鎖時刻のスケジューリング
        LocalDateTime closeTime = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                cl.wlCloseTime,
                0
        );
        if (closeTime.isBefore(start))
            closeTime = closeTime.plusDays(1L);

        scheduler.scheduleSyncDelayedTask(pl, new Runnable() {
                @Override
                public void run() {
                    wlTimeTrg = false;
                    if (!wl.isEnable) {
                        wl.toggleWlist();
                    }
                }
            }, Duration.between(now, closeTime).toSeconds());

        // 現在時刻と比較してOpenの時刻帯なら開けるフラグを立てておく
        if (openTime.isAfter(closeTime) && now.isAfter(openTime)) {
            wlTimeTrg = true;
        }
    }

    // WLTimerのステータス
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("whitelist#status")) return false;

        String view = "";
        view += "現在のホワイトリスト状況: ";
        view += wl.isEnable ? "有効" : "無効";
        view += "\n常時開放状況: ";
        view += wlTimeTrg ? "有効" : "無効";
        view += "\n常時開放時刻: ";
        view += cl.wlOpenTime + ":00 ~ " + cl.wlCloseTime + ":00";

        sender.sendMessage(view);
        return true;
    }
}
