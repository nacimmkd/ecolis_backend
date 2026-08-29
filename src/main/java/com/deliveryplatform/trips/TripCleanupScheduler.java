package com.deliveryplatform.trips;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TripCleanupScheduler {

    private final TripRepository tripRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireOldTrips() {
        var expired = tripRepository.findByStateInAndDepartureDateBefore(
                List.of(TripState.PUBLISHED, TripState.ACTIVE, TripState.FULL),
                LocalDate.now()
        );

        expired.forEach(Trip::expire);

        tripRepository.saveAll(expired);
    }
}
