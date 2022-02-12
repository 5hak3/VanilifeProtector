package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class OpenCloseNotifier {
    private static URL url = null;
    private static final JsonPrimitive uname;
    private static final JsonPrimitive open;
    private static final JsonPrimitive close;
    private static final ArrayList<String> taskQueue;
    private static boolean flg;

    static {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("VanilifeProtector");
            assert plugin != null;
            File file = new File(plugin.getDataFolder(), "config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            url = new URL(Objects.requireNonNull(config.getString("webhook.url")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        uname = new JsonPrimitive("ばにらいふ！開閉通知");
        open = new JsonPrimitive("✅ばにらいふ！が開きました！");
        close = new JsonPrimitive("\uD83D\uDED1ばにらいふ！が閉じました！");
        taskQueue = new ArrayList<>();
        flg = false;
    }

    public static void open() {
        JsonObject post = new JsonObject();
        post.add("username", uname);
        post.add("content", open);
        queue(post.toString());
    }

    public static void close() {
        JsonObject post = new JsonObject();
        post.add("username", uname);
        post.add("content", close);
        queue(post.toString());
    }

    public static void queue(String json) {
        Bukkit.getLogger().info(json + "がキューに入りました．");
        if (flg) {
            taskQueue.add(json);
            return;
        }
        taskQueue.add(json);
        flg = true;
        Bukkit.getScheduler().runTaskLater(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("VanilifeProtector")),
                () -> {
                    requestWebHook(taskQueue.get(taskQueue.size()-1));
                    Bukkit.getLogger().info(taskQueue.get(taskQueue.size()-1) + "が送信されました．");
                    taskQueue.clear();
                    flg = false;
                },
                20*10
        );
    }

    private static void requestWebHook(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("VanilifeProtector")),
                () -> {
                    try {
                        Bukkit.getServer().getLogger().info("Start Discord Sending...");
                        final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                        con.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                        con.addRequestProperty("User-Agent", "VanilifeNotifier/1.0");
                        con.setDoOutput(true);
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Legth", String.valueOf(json.length()));
                        final OutputStream stream = con.getOutputStream();
                        stream.write(json.getBytes(StandardCharsets.UTF_8));
                        stream.flush();
                        stream.close();
                        con.disconnect();
                        con.getResponseCode();
                        Bukkit.getServer().getLogger().info("Finish Discord Sending!\n" + json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        });
    }
}
