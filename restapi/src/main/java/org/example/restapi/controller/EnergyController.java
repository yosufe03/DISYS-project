package org.example.restapi.controller;

import org.example.restapi.dto.CurrentPercentageDto;
import org.example.restapi.dto.UsageDto;
import org.example.restapi.service.EnergyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

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
    public List<UsageDto> getHistoricalUsage(
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to
    ) {
        if (from.isAfter(to)) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }

        return energyService.getHistoricalUsage(from, to);
    }
}
