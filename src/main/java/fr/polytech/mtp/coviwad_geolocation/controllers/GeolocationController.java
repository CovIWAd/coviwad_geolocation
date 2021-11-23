package fr.polytech.mtp.coviwad_geolocation.controllers;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import fr.polytech.mtp.coviwad_geolocation.services.GeolocationKafkaService;
import fr.polytech.mtp.coviwad_geolocation.utils.GeolocationUtils;
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

    @Autowired
    private KafkaTemplate<String, Geolocation> geolocationKafkaTemplate;

    @Autowired
    private ConsumerFactory<String, Geolocation> geolocationConsumerFactory;

    @Autowired
    GeolocationKafkaService geolocationKafkaService;

    //TODO : POST /positive body : geolocation
    //aller lire dans kafka les localisation de -10m sur les 5 derniers jours
    //enregistrer les localisation "cas contacts" dans la bd + envoyer un mail aux personnes "cas contact"
    @PostMapping("/positive")
    public void addPositiveUserGeolocation(Principal principal)
    {
        String idUserCovid = "";
        if(principal != null && principal.getName().length() > 0) {
            idUserCovid = principal.getName();
            //find user potential covided + save their locations that are risky
            Set<String> usersToWarn = geolocationKafkaService.getUsersPotentialCovid(geolocationConsumerFactory, geolocationRepository, idUserCovid);
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
