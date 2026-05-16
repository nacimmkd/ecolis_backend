package com.deliveryplatform.profiles.dto;

public interface ProfileStatsProjection {
    Double getAvgRating();
    Long getReviewCount();
    Long getCompletedTrips();
    Long getDeliveredParcels();
}
