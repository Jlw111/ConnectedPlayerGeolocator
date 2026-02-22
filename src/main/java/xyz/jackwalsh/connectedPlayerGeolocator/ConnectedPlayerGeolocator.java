package xyz.jackwalsh.connectedPlayerGeolocator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public final class ConnectedPlayerGeolocator extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException, InterruptedException {
        String ipinfoToken = Objects.requireNonNull(getConfig().getString("ip-info-token")).strip();

        String joinedPlayerName = event.getPlayer().getName();
        //noinspection DataFlowIssue
        String joinedPlayerIP = event.getPlayer().getAddress().getHostString();

        HttpClient client1 = HttpClient.newHttpClient();
        HttpRequest ipLookup1 = HttpRequest.newBuilder().uri(URI.create("https://ipinfo.io/" + joinedPlayerIP + "/city?token=" + ipinfoToken)).GET().build();
        HttpResponse<String> response1 = client1.send(ipLookup1, HttpResponse.BodyHandlers.ofString());
        client1.close();
        String cityName = response1.body().strip();

        HttpClient client2 = HttpClient.newHttpClient();
        HttpRequest ipLookup2 = HttpRequest.newBuilder().uri(URI.create("https://ipinfo.io/" + joinedPlayerIP + "/region?token=" + ipinfoToken)).GET().build();
        HttpResponse<String> response2 = client2.send(ipLookup2, HttpResponse.BodyHandlers.ofString());
        client2.close();
        String regionName = response2.body().strip();

        HttpClient client3 = HttpClient.newHttpClient();
        HttpRequest ipLookup3 = HttpRequest.newBuilder().uri(URI.create("https://ipinfo.io/" + joinedPlayerIP + "/country?token=" + ipinfoToken)).GET().build();
        HttpResponse<String> response3 = client3.send(ipLookup3, HttpResponse.BodyHandlers.ofString());
        client2.close();
        String countryName = response3.body().strip();

        String printString = "Player " + joinedPlayerName + " joined from " + cityName + ", " + regionName + ", " + countryName;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(printString);
        }
        getLogger().info(printString);
    }
}