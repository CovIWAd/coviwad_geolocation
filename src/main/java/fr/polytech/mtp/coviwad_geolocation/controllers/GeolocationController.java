package fr.polytech.mtp.coviwad_geolocation.controllers;

import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geolocation")
public class GeolocationController {
    @Autowired
    private GeolocationRepository geolocationRepository;
}
