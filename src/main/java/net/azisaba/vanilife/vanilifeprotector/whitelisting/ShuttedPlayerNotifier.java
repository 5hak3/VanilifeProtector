package net.azisaba.vanilife.vanilifeprotector.whitelisting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ShuttedPlayerNotifier {
    private static final URI uri;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("VanilifeProtector");
        assert plugin != null;
        File file = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        uri = URI.create(Objects.requireNonNull(config.getString("webhook.url2")));
    }

    public static void send(OfflinePlayer player, int memberCount) {
        String mcid;
        try {
            JsonObject mojang = (JsonObject) new JsonParser().parse(Unirest.get("https://sessionserver.mojang.com/session/minecraft/profile/"+player.getUniqueId()).asString().getBody());
            if (mojang.get("error") != null) mcid = "unknown";
            else {
                mcid = mojang.get("name").toString();
                mcid = mcid.substring(1, mcid.length() - 1);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            mcid = "unknown";
        }
        JsonObject post = new JsonObject();
        post.add("content",
                new JsonPrimitive("以下のプレイヤーの接続が遮断されました．\n" + String.format("%s (%s) (%d人目)", mcid, player.getUniqueId(), memberCount))
        );
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getLogger().info("Start Discord Sending...");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .method("POST", HttpRequest.BodyPublishers.ofString(post.toString()))
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
