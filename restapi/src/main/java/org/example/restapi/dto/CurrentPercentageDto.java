package org.example.restapi.dto;

import java.time.Instant;

public record CurrentPercentageDto(
        Instant timestamp,
        double communityDepleted,
        double gridPortion
) {}
