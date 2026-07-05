package com.deliveryplatform.matching;

public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_DEGREE    = 111.32;

    private GeoUtils() {}

    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * Distance minimale d'un point P au segment AB (en km).
     * Projette P sur AB et calcule la haversine au point projeté.
     */
    public static double pointToSegmentDistance(double pLat, double pLng,
                                                double aLat, double aLng,
                                                double bLat, double bLng) {
        double dx = bLat - aLat;
        double dy = bLng - aLng;
        if (dx == 0 && dy == 0) {
            return haversine(pLat, pLng, aLat, aLng);
        }
        double t = Math.max(0, Math.min(1,
                ((pLat - aLat) * dx + (pLng - aLng) * dy) / (dx * dx + dy * dy)
        ));
        return haversine(pLat, pLng, aLat + t * dx, aLng + t * dy);
    }

    /**
     * Calcule une bounding box carrée autour d'un point,
     * utilisée pour le pré-filtre SQL.
     */
    public static BoundingBox boundingBox(double lat, double lng, double radiusKm) {
        double deltaLat = radiusKm / KM_PER_DEGREE;
        double deltaLng = radiusKm / (KM_PER_DEGREE * Math.cos(Math.toRadians(lat)));
        return new BoundingBox(lat - deltaLat, lat + deltaLat, lng - deltaLng, lng + deltaLng);
    }

    public record BoundingBox(double minLat, double maxLat, double minLng, double maxLng) {}

}