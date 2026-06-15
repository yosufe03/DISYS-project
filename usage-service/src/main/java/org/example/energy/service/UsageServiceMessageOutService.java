package org.example.energy.service;

import org.example.energy.messaging.dto.UsageUpdateMessageOutDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceMessageOutService {
    private final RabbitTemplate rabbitTemplate;
    private final String usageUpdatedQueue;

    public UsageServiceMessageOutService(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue.usage-updated}") String usageUpdatedQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.usageUpdatedQueue = usageUpdatedQueue;
    }

    public void sendUsageUpdatedMessage(UsageUpdateMessageOutDto message) {
        rabbitTemplate.convertAndSend(
                usageUpdatedQueue,
                message
        );

        System.out.printf(
                "[energy.usage.updated.queue] - Sent message: type=%s, hour=%s, produced=%.4f, communityUsed=%.4f, gridUsed=%.4f%n",
                message.type(),
                message.hour(),
                message.communityProduced(),
                message.communityUsed(),
                message.gridUsed()
        );
    }
}
