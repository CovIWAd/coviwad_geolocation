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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/geolocation")
public class GeolocationController {
    @Autowired
    private GeolocationRepository geolocationRepository;

    //Autowired doesn't work
    private final KafkaTemplate<String, Geolocation> producer = KafkaConfiguration.geolocationKafkaTemplate();
    private final Consumer<String,Geolocation> consumer = KafkaConfiguration.geolocationConsumerFactory().createConsumer();

    @Autowired
    GeolocationKafkaService geolocationKafkaService;

    @PostMapping("/positive")
    public void addPositiveUserGeolocation(Principal principal)
    {
        String idUserCovid = "";
        if(principal != null && principal.getName().length() > 0) {
            idUserCovid = principal.getName();
            //find user potential covided + save their locations that are risky
            Set<String> usersToWarn = geolocationKafkaService.getUsersPotentialCovid(consumer, geolocationRepository, idUserCovid);
            // Now send mails to potential users covided
            if(usersToWarn.size() > 0) {
                geolocationKafkaService.sendMailToCasContact(usersToWarn);
            }
        }
    }

    @PostMapping
    public Geolocation addUserGeolocation(@Valid @RequestBody Geolocation geolocation )
    {
        producer.send("geolocation_topic", geolocation);
        return geolocation;
    }

}
