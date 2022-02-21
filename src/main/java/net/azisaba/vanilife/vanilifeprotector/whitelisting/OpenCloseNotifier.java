package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class OpenCloseNotifier {
    private static final URI uri;
    private static final JsonPrimitive open;
    private static final JsonPrimitive close;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("VanilifeProtector");
        assert plugin != null;
        File file = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        uri = URI.create(Objects.requireNonNull(config.getString("webhook.url")));
        open = new JsonPrimitive("https://media.discordapp.net/attachments/943483083536105512/943483806759944222/open_vanilife.png");
        close = new JsonPrimitive("https://media.discordapp.net/attachments/943483083536105512/943483806231433246/close_vanilife.png");
    }

    public static void open() {
        JsonObject post = new JsonObject();
        post.add("content", open);
        requestWebHook(post.toString());
    }

    public static void close() {
        JsonObject post = new JsonObject();
        post.add("content", close);
        requestWebHook(post.toString());
    }

    private static void requestWebHook(String json) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getLogger().info("Start Discord Sending...");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .build();
                try {
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getLogger().info("Finish Discord Sending!\n");
            }
        }.runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("VanilifeProtector")));
    }
}
