package org.example.energy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "usage_records")
public class UsageRecord {
    @Id
    private Instant hour;

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    protected UsageRecord() {
    }

    public UsageRecord(Instant hour) {
        this.hour = hour;
    }

    public Instant getHour() {
        return hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public double getCommunityPool() {
        return communityProduced - communityUsed;
    }

    public void addProduction(double kwh) {
        if (kwh <= 0.0) {
            return;
        }

        communityProduced += kwh;
    }

    public void addDemand(double kwh) {
        if (kwh <= 0.0) {
            return;
        }
        double communityPart = Math.min(getCommunityPool(), kwh);
        double gridPart = kwh - communityPart;

        communityUsed += communityPart;
        gridUsed += gridPart;
    }
}
