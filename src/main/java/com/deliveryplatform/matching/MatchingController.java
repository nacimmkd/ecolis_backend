package com.deliveryplatform.matching;

import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingQueryService matchingQueryService;

    @GetMapping
    public ResponseEntity<List<MatchResultDto>> getMatchingTrips(
            @RequestParam @NotNull UUID parcelId,
            @RequestParam @NotNull LocalDate date,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        return ResponseEntity.ok(matchingQueryService.findMatchingTrips(parcelId, date, user.getId()));
    }
}