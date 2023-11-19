package tech.nicecraftz.antiproxy.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class RequestUtil {
    private static final String API_URL = "https://proxycheck.io/v2/%ip_addr%?vpn=1?key=%api_key%";

    public boolean usesProxy(String apiKey, String address) {
        String url = API_URL.replace("%ip_addr%", address).replace("%api_key%", apiKey);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .headers("User-Agent", "AntiProxyPaper")
                .uri(URI.create(url))
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        String status = jsonElement.getAsJsonObject().get("status").getAsString();
        if (!status.equalsIgnoreCase("ok")) return false;

        String usesProxy = jsonElement.getAsJsonObject().get(address).getAsJsonObject().get("proxy").getAsString();
        return usesProxy.equalsIgnoreCase("yes");
    }

    public CompletableFuture<Boolean> usesProxyAsync(String apiKey, String address) {
        return CompletableFuture.supplyAsync(() -> usesProxy(apiKey, address));
    }

}
