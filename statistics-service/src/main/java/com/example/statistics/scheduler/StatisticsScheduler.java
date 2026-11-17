package com.example.statistics.scheduler;

import com.example.statistics.service.StatisticsCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticsScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(StatisticsScheduler.class);

    private final StatisticsCollectionService statisticsCollectionService;

    public StatisticsScheduler(
            StatisticsCollectionService statisticsCollectionService
    ) {
        this.statisticsCollectionService = statisticsCollectionService;
    }

    @Scheduled(
            cron = "${statistics.collection.cron:0 0 2 * * *}",
            zone = "${statistics.collection.zone:UTC}"
    )
    public void collectDailyStatistics() {
        log.info("Starting daily statistics collection");

        statisticsCollectionService.collectStatistics();

        log.info("Daily statistics collection completed successfully");
    }
}
