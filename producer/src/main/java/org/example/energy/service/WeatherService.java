package org.example.energy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

@Service
public class WeatherService {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String weatherUrl;

    private Instant lastLoaded = Instant.EPOCH;
    private double cachedSolarFactor = 0.5;

    public WeatherService(@Value("${producer.weather-url}") String weatherUrl) {
        this.weatherUrl = weatherUrl;
    }

    public synchronized double currentSolarFactor() {
        Instant now = Instant.now();

        if (Duration.between(lastLoaded, now).toMinutes() < 10) {
            return cachedSolarFactor;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(weatherUrl))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            String body = http.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JsonNode current = mapper.readTree(body).path("current");
            int cloudCover = current.path("cloud_cover").asInt(50);

            cachedSolarFactor = 1.0 - (cloudCover / 100.0);
        } catch (Exception ignored) {
            // Offline fallback: still plausible, but the application keeps running.
            cachedSolarFactor = 0.4;
        }

        lastLoaded = now;
        return cachedSolarFactor;
    }
}
