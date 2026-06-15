package org.example.energy.messaging.dto;

import java.time.Instant;

public record UserMessageOutDto(
        String type,
        String association,
        double kwh,
        Instant datetime
) {}
