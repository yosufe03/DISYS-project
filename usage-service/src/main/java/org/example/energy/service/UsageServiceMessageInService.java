package org.example.energy.service;

import org.example.energy.entity.UsageRecord;
import org.example.energy.messaging.dto.EnergyMessageInDto;
import org.example.energy.messaging.dto.UsageUpdateMessageOutDto;
import org.example.energy.repository.UsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UsageServiceMessageInService {
    private final UsageRepository usageRepository;
    private final UsageServiceMessageOutService usageServiceMessageOutService;
    private final String energyQueue;

    public UsageServiceMessageInService(UsageRepository usageRepository,
                                        UsageServiceMessageOutService usageServiceMessageOutService,
                                        @Value("${rabbitmq.queue.energy}") String energyQueue) {
        this.usageRepository = usageRepository;
        this.usageServiceMessageOutService = usageServiceMessageOutService;
        this.energyQueue = energyQueue;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.energy}")
    public void onEnergyMessage(EnergyMessageInDto message) {
        System.out.printf(
                "[%s] - Received message: type=%s, association=%s, kwh=%.4f, datetime=%s%n",
                energyQueue,
                message.type(),
                message.association(),
                message.kwh(),
                message.datetime()
        );

        updateHour(message.datetime(), message.type(), message.kwh());
    }

    private synchronized void updateHour(Instant timestamp, String messageType, double kwh) {
        Instant hour = timestamp.truncatedTo(ChronoUnit.HOURS);

        UsageRecord record = usageRepository
                .findById(hour)
                .orElseGet(() -> new UsageRecord(hour));

        switch (messageType) {
            case "PRODUCER" -> record.addProduction(kwh);
            case "USER" -> record.addDemand(kwh);
            default -> throw new IllegalArgumentException("Unsupported energy message type: " + messageType);
        }

        usageRepository.save(record);

        UsageUpdateMessageOutDto update = new UsageUpdateMessageOutDto(
                "USAGE_UPDATE",
                record.getHour(),
                record.getCommunityProduced(),
                record.getCommunityUsed(),
                record.getGridUsed()
        );

        usageServiceMessageOutService.sendUsageUpdatedMessage(update);

        System.out.printf(
                "USAGE_RECORD Updated %s: produced=%.4f, communityUsed=%.4f, gridUsed=%.4f, pool=%.4f%n",
                record.getHour(),
                record.getCommunityProduced(),
                record.getCommunityUsed(),
                record.getGridUsed(),
                record.getCommunityPool()
        );
    }
}
