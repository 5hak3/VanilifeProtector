package net.azisaba.vanilife.vanilifeprotector.jail;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class VanilifeJailNotifier {
    private static final URI uri;
    private static final Plugin plugin;

    static {
        plugin = Bukkit.getPluginManager().getPlugin("VanilifeProtector");
        assert plugin != null;
        File file = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        uri = URI.create(Objects.requireNonNull(config.getString("webhook.url3")));
    }

    public static void send(Player sender, Player target, String reason) {
        JsonObject post = new JsonObject();
        post.add("username", new JsonPrimitive("VanilifeJailNotifier"));
        JsonArray embs = new JsonArray();
        JsonObject emb = new JsonObject();
        emb.add("title", new JsonPrimitive("プレイヤーがJailされました！"));

        JsonArray fields = new JsonArray();
        JsonObject f1 = new JsonObject();
        f1.add("name", new JsonPrimitive("実行者"));
        f1.add("value", new JsonPrimitive(sender.getName()));
        f1.add("inline", new JsonPrimitive(true));
        JsonObject f2 = new JsonObject();
        f2.add("name", new JsonPrimitive("対象者"));
        f2.add("value", new JsonPrimitive(target.getName()));
        f2.add("inline", new JsonPrimitive(true));
        JsonObject f3 = new JsonObject();
        f3.add("name", new JsonPrimitive("理由"));
        f3.add("value", new JsonPrimitive(reason));
        f3.add("inline", new JsonPrimitive(false));
        fields.add(f1);
        fields.add(f2);
        fields.add(f3);
        emb.add("fields", fields);
        embs.add(emb);
        post.add("embeds", embs);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "VP/1.0");
                    con.addRequestProperty("Content-Type", "application/JSON; charset=utf-8");
                    con.setRequestProperty("Content-Length", String.valueOf(post.toString().length()));
                    con.setDoOutput(true);
                    OutputStream ost = con.getOutputStream();
                    ost.write(post.toString().getBytes(StandardCharsets.UTF_8));
                    ost.flush();
                    ost.close();
                    con.disconnect();
                    con.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
