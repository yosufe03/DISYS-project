package org.example.energy.repository;

import org.example.energy.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface UsageRepository extends JpaRepository<UsageRecord, Instant> {
}
