package fr.polytech.mtp.coviwad_geolocation.repositories;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GeolocationRepository extends JpaRepository<Geolocation, Long> {
    List<Geolocation> findByUserId(String userId);
    List<Geolocation> findAllByGeolocationDateBetween(Date timeStart, Date timeEnd);
}
