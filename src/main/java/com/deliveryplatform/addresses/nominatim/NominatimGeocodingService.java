package com.deliveryplatform.addresses.nominatim;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.addresses.AddressMapper;
import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.addresses.GeocodingService;
import com.deliveryplatform.common.exceptions.ExternalServiceException;
import com.deliveryplatform.common.exceptions.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NominatimGeocodingService implements GeocodingService {

    private final WebClient nominatimWebClient;
    private final AddressMapper addressMapper;

    @Override
    public GeocodedAddress geocode(Address address) {
        return fetchFirstResult(address)
                .map(result -> addressMapper.toGeocodedAddress(
                        address,
                        Double.parseDouble((String) result.get("lat")),
                        Double.parseDouble((String) result.get("lon"))
                ))
                .orElseThrow(() -> new UnprocessableEntityException("The provided address could not be found"));
    }

    private Optional<Map<String, Object>> fetchFirstResult(Address address) {
        try {
            List<Map<String, Object>> results = nominatimWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", address.toString())
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            return results == null || results.isEmpty()
                    ? Optional.empty()
                    : Optional.of(results.get(0));

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(getClass(), "HTTP Error" + e.getStatusCode());
        }
    }
}