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
import java.time.LocalDateTime;

// 前提として1日1回は再起動されることとしている
// そうでない場合は、実行されたタイミングで再スケジューリングが必要
public class WhitelistTimer implements CommandExecutor {
    public boolean wlTimeTrg;
    private final ConfigLoader cl;
    private final Plugin pl;
    private final Whitelist wl;
//    private BukkitScheduler schedular;

    public WhitelistTimer(ConfigLoader cl, Plugin pl, Whitelist wl) {
        this.wlTimeTrg = false;
        this.cl = cl;
        this.pl = pl;
        this.wl = wl;

        runTimer();
    }

    public void runTimer() {
        LocalDateTime now = LocalDateTime.now();
        Bukkit.getLogger().info("現在時刻: " + now);

//        // サーバ起動時刻（1日の始まり）
//        LocalDateTime start = LocalDateTime.of(
//                now.getYear(),
//                now.getMonth(),
//                now.getDayOfMonth(),
//                5,
//                0
//        );
//        if (now.isBefore(start)) start = start.minusDays(1);
//        Bukkit.getLogger().info("サーバ起動時刻(推定): " + start);

        BukkitScheduler schedular = Bukkit.getScheduler();

        // WL開放時刻のスケジューリング
        LocalDateTime openTime = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                cl.wlOpenTimeHour,
                cl.wlOpenTimeMin
        );
        // 現在時刻と比較して、開放時刻が前なら開放時刻を次の日の同時刻にする
        if (openTime.isBefore(now)) openTime = openTime.plusDays(1L);
        Bukkit.getLogger().info("開放時刻: " + openTime);

        long openSec = Duration.between(now, openTime).toSeconds();
        Bukkit.getLogger().info("開放時刻までの秒数" + openSec);
        schedular.scheduleSyncDelayedTask(pl, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("常時開放フラグがONになりました．");
                wlTimeTrg = true;
                wl.offWlist();
            }
        }, openSec * 20);

        // WL閉鎖時刻のスケジューリング
        LocalDateTime closeTime = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                cl.wlCloseTimeHour,
                cl.wlCloseTimeMin
        );
        // 現在時刻と比較して、閉鎖時刻が前なら閉鎖時刻を次の日の同時刻にする
        if (closeTime.isBefore(now)) closeTime = closeTime.plusDays(1L);
        Bukkit.getLogger().info("閉鎖時刻: " + closeTime);

        long closeSec = Duration.between(now, closeTime).toSeconds();
        Bukkit.getLogger().info("閉鎖時刻までの秒数" + closeSec);
        schedular.scheduleSyncDelayedTask(pl, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("常時開放フラグがOFFになりました．");
                wlTimeTrg = false;
                wl.onWlist();
            }
        }, closeSec * 20);

        // 次のイベントが閉鎖なら今は開いていることとする
        if (closeSec < openSec) {
            wlTimeTrg = true;
            Bukkit.getLogger().info("開放時間中なため常時開放フラグをONにしました。");
        }
        else wlTimeTrg = false;
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
        view += String.format("\n常時開放時刻: %02d:%02d ~ %02d:%02d",
                cl.wlOpenTimeHour, cl.wlOpenTimeMin, cl.wlCloseTimeHour, cl.wlCloseTimeMin);

        LocalDateTime now = LocalDateTime.now();
        view += String.format("\n現在時刻: %02d:%02d", now.getHour(), now.getMinute());

        sender.sendMessage(view);
        return true;
    }
}
