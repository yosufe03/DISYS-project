package org.example.energy.service;

import org.example.energy.entity.PercentageRecord;
import org.example.energy.messaging.dto.UsageUpdateMessageInDto;
import org.example.energy.repository.PercentageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageUpdateMessageInService {
    private final PercentageRepository percentageRepository;

    public UsageUpdateMessageInService(PercentageRepository percentageRepository) {
        this.percentageRepository = percentageRepository;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.usage-updated}")
    public void onUsageUpdated(UsageUpdateMessageInDto message) {
        System.out.printf(
                "[energy.usage.updated.queue] - Received message: type=%s, hour=%s, produced=%.4f, communityUsed=%.4f, gridUsed=%.4f%n",
                message.type(),
                message.hour(),
                message.communityProduced(),
                message.communityUsed(),
                message.gridUsed()
        );

        double produced = message.communityProduced();
        double communityUsed = message.communityUsed();
        double gridUsed = message.gridUsed();
        double totalUsed = communityUsed + gridUsed;

        double communityDepleted = produced == 0.0
                ? 0.0
                : (communityUsed / produced) * 100.0;

        double gridPortion = totalUsed == 0.0
                ? 0.0
                : (gridUsed / totalUsed) * 100.0;

        PercentageRecord record = new PercentageRecord(
                message.hour(),
                communityDepleted,
                gridPortion
        );

        percentageRepository.deleteAll();
        percentageRepository.save(record);

        System.out.printf(
                "PERCENTAGE_RECORD Updated %s: communityDepleted=%.2f%%, gridPortion=%.2f%%%n",
                record.getHour(),
                record.getCommunityDepleted(),
                record.getGridPortion()
        );
    }
}
