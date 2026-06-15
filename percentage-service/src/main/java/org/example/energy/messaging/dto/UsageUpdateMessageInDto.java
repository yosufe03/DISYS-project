package org.example.energy.messaging.dto;

import java.time.Instant;

public record UsageUpdateMessageInDto(
        String type,
        Instant hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {}
