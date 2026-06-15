package org.example.restapi.service;

import org.example.restapi.dto.CurrentPercentageDto;
import org.example.restapi.dto.HistoricalUsageDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class EnergyService {
    private final JdbcTemplate jdbcTemplate;

    public EnergyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CurrentPercentageDto getCurrentPercentage() {
        return jdbcTemplate.query("""
                        select hour, community_depleted, grid_portion
                        from percentage_records
                        order by hour desc
                        limit 1
                        """,
                rs -> {
                    if (!rs.next()) {
                        return emptyCurrent();
                    }
                    return new CurrentPercentageDto(
                            rs.getTimestamp("hour").toInstant(),
                            rs.getDouble("community_depleted"),
                            rs.getDouble("grid_portion")
                    );
                });
    }

    public HistoricalUsageDto getHistoricalUsage(Instant from, Instant to) {
        return jdbcTemplate.query("""
                        select
                            coalesce(sum(community_produced), 0) as community_produced,
                            coalesce(sum(community_used), 0) as community_used,
                            coalesce(sum(grid_used), 0) as grid_used
                        from usage_records
                        where hour between ? and ?
                        """,
                rs -> {
                    rs.next();
                    return new HistoricalUsageDto(
                            from,
                            to,
                            rs.getDouble("community_produced"),
                            rs.getDouble("community_used"),
                            rs.getDouble("grid_used")
                    );
                },
                Timestamp.from(from),
                Timestamp.from(to));
    }

    private CurrentPercentageDto emptyCurrent() {
        return new CurrentPercentageDto(Instant.now(), 0.0, 0.0);
    }
}
