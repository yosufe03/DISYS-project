package org.example.energy.repository;

import org.example.energy.entity.PercentageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface PercentageRepository extends JpaRepository<PercentageRecord, Instant> {
}
