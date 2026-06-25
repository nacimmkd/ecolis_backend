package com.deliveryplatform.addresses.nominatim;

import com.deliveryplatform.addresses.Coordinates;
import com.deliveryplatform.addresses.GeocodingPort;
import com.deliveryplatform.common.exceptions.ExternalServiceException;
import com.deliveryplatform.common.exceptions.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class NominatimGeocoding implements GeocodingPort {

    private final WebClient nominatimWebClient;

    @Override
    public Coordinates geocode(String fullAddress) {
        return fetchFirstResult(fullAddress)
                .map(result -> new Coordinates(
                        Double.parseDouble(result.lat()),
                        Double.parseDouble(result.lon())
                ))
                .orElseThrow(() -> new UnprocessableEntityException("The address could not be found"));
    }

    private Optional<NominatimResponse> fetchFirstResult(String address) {
        try {
            List<NominatimResponse> results = nominatimWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", address)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToFlux(NominatimResponse.class)
                    .collectList()
                    .block();

            return results == null || results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(getClass(), "HTTP Error: " + e.getStatusCode());
        }
    }
}