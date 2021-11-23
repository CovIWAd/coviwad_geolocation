package fr.polytech.mtp.coviwad_geolocation.utils;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;

import java.util.Date;
import java.util.List;

public class GeolocationUtils {

    //SOURCE : https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    public static double distanceBetween2Points(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000;
    }

    public static long daysInMillis(int days){
        //day in milliseconds
        long millis = 1000 * 60 * 60 * 24;
        return days * millis;
    }

    public static List<Geolocation> getGeolocationsWithin5Days(GeolocationRepository geolocationRepository) {
        //5 days ago
        Date date = new Date(System.currentTimeMillis() - (daysInMillis(5)));
        Date today = new Date();
        return geolocationRepository.findAllByGeolocationDateBetween(date,today);
    }


}
