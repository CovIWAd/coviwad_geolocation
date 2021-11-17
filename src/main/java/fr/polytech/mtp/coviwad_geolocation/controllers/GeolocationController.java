package fr.polytech.mtp.coviwad_geolocation.controllers;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/geolocation")
public class GeolocationController {
    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private KafkaTemplate<String, Geolocation> geolocationKafkaTemplate;

    @PostMapping
    public Geolocation addUserGeolocation(Principal principal, @Valid @RequestBody Geolocation geolocation )
    {
        String userId = "theId0";
        if(principal != null && principal.getName().length() > 0) userId = principal.getName();
        Geolocation document = new Geolocation(userId, geolocation.getLatitude(), geolocation.getLongitude(), geolocation.getTimestamp());
        Geolocation saved =  geolocationRepository.save(document);
        geolocationKafkaTemplate.send("geolocation_added", document);
        return saved;
    }

}
