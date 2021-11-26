package fr.polytech.mtp.coviwad_geolocation.controllers;

import fr.polytech.mtp.coviwad_geolocation.config.KafkaConfiguration;
import fr.polytech.mtp.coviwad_geolocation.dtos.PositiveTestDTO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RolesAllowed({"user","admin"})
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
    public void addPositiveUserGeolocation(Principal principal, @RequestBody(required = false) PositiveTestDTO testDate) {
        if(principal.getName() != null && principal.getName().length() > 0) {
            //find user potential covided + save their locations that are risky
            Set<String> usersToWarn;
            if(testDate.getTestDate() != null){
                usersToWarn = geolocationKafkaService.getUsersPotentialCovid(consumer, geolocationRepository, principal.getName(), testDate.getTestDate());
            } else {
                usersToWarn = geolocationKafkaService.getUsersPotentialCovid(consumer, geolocationRepository, principal.getName(), null);
            }
            // Now send mails to potential users covided
            if(usersToWarn.size() > 0) {
                geolocationKafkaService.sendMailToCasContact(usersToWarn);
            }
        }
    }

    @PostMapping
    public Geolocation addUserGeolocation(@Valid @RequestBody Geolocation geolocation )
    {
       geolocationKafkaTemplate.send("geolocation_topic", geolocation);
        return geolocation;
    }

}
