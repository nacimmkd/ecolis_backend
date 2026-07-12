package com.deliveryplatform.requests;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class RequestCleanupScheduler {

    private final RequestRepository requestRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireOldPendingRequests() {
        var cutoff = OffsetDateTime.now().minusDays(7);
        var expired = requestRepository.findByStatusAndRequestedAtBefore(RequestState.PENDING, cutoff);

        expired.forEach(request -> request.reject("Expired - no response from carrier"));

        requestRepository.saveAll(expired);
    }
}