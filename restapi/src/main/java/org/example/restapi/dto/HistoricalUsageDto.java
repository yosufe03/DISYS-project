package org.example.restapi.dto;

import java.time.Instant;

public record HistoricalUsageDto(
        Instant startTime,
        Instant endTime,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {}
