package fr.polytech.mtp.coviwad_geolocation.controllers;

import fr.polytech.mtp.coviwad_geolocation.config.KafkaConfiguration;
import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import fr.polytech.mtp.coviwad_geolocation.services.GeolocationKafkaService;
import fr.polytech.mtp.coviwad_geolocation.utils.GeolocationUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RolesAllowed({"user"})
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/geolocation")
public class GeolocationController {
    @Autowired
    private GeolocationRepository geolocationRepository;

    //Autowired doesn't work
    private final KafkaTemplate<String, Geolocation> producer = KafkaConfiguration.geolocationKafkaTemplate();
    private final Consumer<String,Geolocation> consumer = KafkaConfiguration.geolocationConsumerFactory().createConsumer();

    @Autowired
    private KafkaTemplate<String, Geolocation> geolocationKafkaTemplate;

    @Autowired
    GeolocationKafkaService geolocationKafkaService;

    @PostMapping("/positive")
    public void addPositiveUserGeolocation(@Valid @RequestBody String userId)
    {
        System.out.println(userId);
        if(userId != null && userId.length() > 0) {
            System.out.println("ICI"+ userId);
            //find user potential covided + save their locations that are risky
            Set<String> usersToWarn = geolocationKafkaService.getUsersPotentialCovid(consumer, geolocationRepository, userId);
            // Now send mails to potential users covided
            if(usersToWarn.size() > 0) {
                geolocationKafkaService.sendMailToCasContact(usersToWarn);
            }
        }
    }

    @PostMapping
    public Geolocation addUserGeolocation(@Valid @RequestBody Geolocation geolocation )
    {
        System.out.println("LAAAA");
        System.out.println(geolocation.getLatitude());
        geolocationKafkaTemplate.send("geolocation_topic", geolocation);
        return geolocation;
    }

}
