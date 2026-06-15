package org.example.energy.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public Queue usageUpdatedQueue(@Value("${rabbitmq.queue.usage-updated}") String usageUpdatedQueue) {
        return new Queue(usageUpdatedQueue, true);
    }
}
