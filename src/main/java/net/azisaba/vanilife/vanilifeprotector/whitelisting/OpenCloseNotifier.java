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
import java.util.Objects;

public class OpenCloseNotifier {
    private static URL url = null;
    private static final JsonPrimitive uname;
    private static final JsonPrimitive open;
    private static final JsonPrimitive close;
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
        open = new JsonPrimitive("ばにらいふ！が開きました！");
        close = new JsonPrimitive("ばにらいふ！が閉じました！");
    }

    public static void open() {
        JsonObject post = new JsonObject();
        post.add("username", uname);
        post.add("content", open);
        requestWebHook(post.toString());
    }

    public static void close() {
        JsonObject post = new JsonObject();
        post.add("username", uname);
        post.add("content", close);
        requestWebHook(post.toString());
    }

    private static void requestWebHook(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("VanilifeProtector")),
                () -> {
                    try {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        });
    }
}
