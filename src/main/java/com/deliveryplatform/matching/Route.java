package com.deliveryplatform.matching;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.trips.TripStop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Représente la route d'un trip (départ → stops → arrivée) et sa géométrie. */
public final class Route {

    private final List<Address> waypoints;

    private Route(List<Address> waypoints) {
        this.waypoints = waypoints;
    }

    public static Route of(Trip trip) {
        List<Address> points = new ArrayList<>();
        points.add(trip.getDepartureAddress());
        trip.getStops().stream()
                .sorted(Comparator.comparingInt(TripStop::getOrder))
                .map(TripStop::getAddress)
                .forEach(points::add);
        points.add(trip.getArrivalAddress());
        return new Route(points);
    }

    /** Distance minimale d'un point à cette route (km). */
    public double distanceTo(double lat, double lng) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Address a = waypoints.get(i);
            Address b = waypoints.get(i + 1);
            double d = GeoUtils.pointToSegmentDistance(
                    lat, lng,
                    a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude()
            );
            if (d < min) min = d;
        }
        return min;
    }

    /** Longueur totale de la route (km). */
    public double length() {
        double total = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Address a = waypoints.get(i);
            Address b = waypoints.get(i + 1);
            total += GeoUtils.haversine(
                    a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude()
            );
        }
        return total;
    }
}