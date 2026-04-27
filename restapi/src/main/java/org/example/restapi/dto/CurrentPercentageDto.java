package org.example.restapi.dto;

import java.time.OffsetDateTime;

public class CurrentPercentageDto {
    private OffsetDateTime hour;
    private double communityPercentage;
    private double gridPercentage;

    public CurrentPercentageDto(OffsetDateTime hour, double communityPercentage, double gridPercentage) {
        this.hour = hour;
        this.communityPercentage = communityPercentage;
        this.gridPercentage = gridPercentage;
    }

    public OffsetDateTime getHour() {
        return hour;
    }

    public void setHour(OffsetDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityPercentage() {
        return communityPercentage;
    }

    public void setCommunityPercentage(double communityPercentage) {
        this.communityPercentage = communityPercentage;
    }

    public double getGridPercentage() {
        return gridPercentage;
    }

    public void setGridPercentage(double gridPercentage) {
        this.gridPercentage = gridPercentage;
    }
}
