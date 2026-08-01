package com.deliveryplatform.bookings;

import com.deliveryplatform.payments.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireOldPendingBookings() {
        var cutoff = OffsetDateTime.now().minusDays(7);
        var expired = bookingRepository.findByStateAndCreatedAtBefore(BookingState.WAITING_FOR_ANSWER, cutoff);

        paymentService.cancelAll(expired.stream().map(Booking::getId).toList());
        expired.forEach(Booking::expire);

        bookingRepository.saveAll(expired);
    }
}
