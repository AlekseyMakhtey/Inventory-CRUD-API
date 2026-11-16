
package com.example.statistics.controller;

import com.example.statistics.dto.StatisticsResponse;
import com.example.statistics.service.StatisticsCollectionService;
import com.example.statistics.service.StatisticsQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsQueryService statisticsQueryService;
    private final StatisticsCollectionService statisticsCollectionService;

    public StatisticsController(
            StatisticsQueryService statisticsQueryService,
            StatisticsCollectionService statisticsCollectionService
    ) {
        this.statisticsQueryService = statisticsQueryService;
        this.statisticsCollectionService = statisticsCollectionService;
    }

    @PostMapping("/collect")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void collectStatistics() {
        statisticsCollectionService.collectStatistics();
    }

    @GetMapping
    public List<StatisticsResponse> getStatistics(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'from' must not be after 'to'"
            );
        }

        return statisticsQueryService.getStatistics(from, to);
    }
}
