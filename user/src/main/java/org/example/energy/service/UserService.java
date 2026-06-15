package org.example.energy.service;

import org.example.energy.messaging.dto.UserMessageOutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    private final UserMessageOutService userMessageOutService;
    private final String association;

    private Instant nextSendTime = Instant.now();

    public UserService(UserMessageOutService userMessageOutService,
                       @Value("${user.association}") String association) {
        this.userMessageOutService = userMessageOutService;
        this.association = association;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendUsageMessage() {
        Instant now = Instant.now();

        if (now.isBefore(nextSendTime)) {
            return;
        }

        double kwh = plausibleDemand(LocalTime.now().getHour());

        UserMessageOutDto message = new UserMessageOutDto(
                "USER",
                association,
                kwh,
                now
        );

        userMessageOutService.sendUserMessage(message);

        int nextDelaySeconds = ThreadLocalRandom.current().nextInt(1, 6);
        nextSendTime = now.plusSeconds(nextDelaySeconds);

        System.out.printf(
                "Next message in %d seconds: kwh=%.4f%n",
                nextDelaySeconds,
                kwh
        );
    }

    private double plausibleDemand(int hour) {
        double base = ThreadLocalRandom.current().nextDouble(0.004, 0.015);

        if (hour >= 6 && hour <= 9) {
            base *= 1.8;
        } else if (hour >= 17 && hour <= 22) {
            base *= 2.4;
        } else if (hour >= 0 && hour <= 5) {
            base *= 0.6;
        }

        return base;
    }
}
