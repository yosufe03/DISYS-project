package org.example.restapi.controller;

import org.example.restapi.dto.CurrentPercentageDto;
import org.example.restapi.dto.HistoricalUsageDto;
import org.example.restapi.service.EnergyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/energy")
public class EnergyController {
    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/current")
    public CurrentPercentageDto getCurrentPercentage() {
        return energyService.getCurrentPercentage();
    }

    @GetMapping("/historical")
    public HistoricalUsageDto getHistoricalUsage(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        if (start.isAfter(end)) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
        return energyService.getHistoricalUsage(start, end);
    }
}
