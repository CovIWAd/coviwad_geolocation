package fr.polytech.mtp.coviwad_geolocation.repositories;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeolocationRepository extends JpaRepository<Geolocation, Long> {
    List<Geolocation> findByUserId(String userId);
}
