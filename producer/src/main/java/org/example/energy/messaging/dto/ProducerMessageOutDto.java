package org.example.energy.messaging.dto;

import java.time.Instant;

public record ProducerMessageOutDto(
        String type,
        String association,
        double kwh,
        Instant datetime
) {}
