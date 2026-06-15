package org.example.energy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "percentage_records")
public class PercentageRecord {
    @Id
    private Instant hour;

    private double communityDepleted;
    private double gridPortion;

    protected PercentageRecord() {
    }

    public PercentageRecord(Instant hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public Instant getHour() {
        return hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }
}
