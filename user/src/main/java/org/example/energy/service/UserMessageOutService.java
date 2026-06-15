package org.example.energy.service;

import org.example.energy.messaging.dto.UserMessageOutDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserMessageOutService {
    private final RabbitTemplate rabbitTemplate;
    private final String energyQueue;

    public UserMessageOutService(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue.energy}") String energyQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.energyQueue = energyQueue;
    }

    public void sendUserMessage(UserMessageOutDto message) {
        rabbitTemplate.convertAndSend(
                energyQueue,
                message
        );

        System.out.printf(
                "[%s] - Sent message: type=%s, association=%s, kwh=%.4f, datetime=%s%n",
                energyQueue,
                message.type(),
                message.association(),
                message.kwh(),
                message.datetime()
        );
    }
}
