package org.example.energy.service;

import org.example.energy.messaging.dto.ProducerMessageOutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProducerService {
    private final ProducerMessageOutService producerMessageOutService;
    private final WeatherService weatherService;
    private final String association;

    private Instant nextSendTime = Instant.now();

    public ProducerService(ProducerMessageOutService producerMessageOutService,
                           WeatherService weatherService,
                           @Value("${producer.association}") String association) {
        this.producerMessageOutService = producerMessageOutService;
        this.weatherService = weatherService;
        this.association = association;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendProductionMessage() {
        Instant now = Instant.now();

        if (now.isBefore(nextSendTime)) {
            return;
        }

        double weatherFactor = weatherService.currentSolarFactor();
        double base = ThreadLocalRandom.current().nextDouble(0.020, 0.080);
        double kwh = base * weatherFactor;

        ProducerMessageOutDto message = new ProducerMessageOutDto(
                "PRODUCER",
                association,
                kwh,
                now
        );

        producerMessageOutService.sendProducerMessage(message);

        int nextDelaySeconds = ThreadLocalRandom.current().nextInt(1, 6);
        nextSendTime = now.plusSeconds(nextDelaySeconds);

        System.out.printf(
                "Next message in %d seconds: kwh=%.4f%n",
                nextDelaySeconds,
                kwh
        );
    }

}
