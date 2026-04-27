package org.example.restapi.service;

import org.example.restapi.dto.CurrentPercentageDto;
import org.example.restapi.dto.UsageDto;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

@Service
public class EnergyService {

    public CurrentPercentageDto getCurrentPercentage() {
        OffsetDateTime hour = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
        SplittableRandom rng = new SplittableRandom();
        double community = 30 + rng.nextDouble() * 40;

        return new CurrentPercentageDto(hour, community, 100 - community);
    }

    public List<UsageDto> getHistoricalUsage(OffsetDateTime from, OffsetDateTime to) {
        OffsetDateTime start = from.truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime end = to.truncatedTo(ChronoUnit.HOURS);
        long hours = ChronoUnit.HOURS.between(start, end) + 1;

        return IntStream.range(0, (int) hours)
                .mapToObj(i -> {
                    OffsetDateTime hour = start.plusHours(i);
                    SplittableRandom rng = new SplittableRandom(hour.toEpochSecond());
                    return new UsageDto(hour, 50 + rng.nextDouble() * 100, 40 + rng.nextDouble() * 80, 30 + rng.nextDouble() * 70);
                })
                .toList();
    }
}
