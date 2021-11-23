package fr.polytech.mtp.coviwad_geolocation.services;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import fr.polytech.mtp.coviwad_geolocation.utils.GeolocationUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GeolocationKafkaService {

    public ConsumerRecords<String, Geolocation> kafkaGetGeolocations(Consumer<String,Geolocation> consumer) {
        //Consumer<String,Geolocation> consumer = geolocationConsumerFactory.createConsumer();
        TopicPartition topicPartition = new TopicPartition("geolocation_added", 0);
        List<TopicPartition> partitions = List.of(topicPartition);
        consumer.assign(partitions);
        // We want to read all the messages
        consumer.seekToBeginning(partitions);
        return consumer.poll(Duration.ofMillis(1000));
    }

    public List<Geolocation> retrieveGeolocationsDaysKafka(ConsumerRecords<String,Geolocation> locations, int days) {
        long currentDate = new Date().getTime();
        Geolocation locationValue;
        long locationDate;
        long diffInMs;
        long diffInDays;
        List<Geolocation> geolocationsCovid = new ArrayList<>();
        for (ConsumerRecord<String, Geolocation> location : locations) {
            locationValue = location.value();
            // If no date is sent, we use the moment the date was saved
            locationDate = locationValue.getGeolocationDate() == null ?
                    location.timestamp() :
                    locationValue.getGeolocationDate().getTime();
            diffInMs = Math.abs(currentDate - locationDate);
            diffInDays = TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
            // We only keep the ones of the considered user, within the last 7 days
            if(diffInDays <= days) {
                geolocationsCovid.add(locationValue);
            }
        }
        return geolocationsCovid;
    }

    //localisations du mec des 3 derniers jours
    public Set<String> getUsersPotentialCovid (ConsumerFactory<String, Geolocation> geolocationConsumerFactory, GeolocationRepository geolocationRepository, String userCovid){
        Consumer<String,Geolocation> consumer = geolocationConsumerFactory.createConsumer();
        ConsumerRecords<String, Geolocation> geolocations = kafkaGetGeolocations(consumer);

        //récupérer les positions du mec en param sur les 3 derniers jours
        List<Geolocation> geolocationsCovid3Days = retrieveGeolocationsDaysKafka(geolocations,3, userCovid);

        List<Geolocation> geolocationsWithin5Days = retrieveGeolocationsDaysKafka(geolocations,5);

        Set<String> usersToWarn = new HashSet<>();
        String newId;
        double distance;
        for(Geolocation susLoc: geolocationsWithin5Days) {
            distance = GeolocationUtils.distanceBetween2Points(
                    susLoc.getLatitude(),
                    susLoc.getLongitude(),
                    geolocCovid.getLatitude(),
                    geolocCovid.getLongitude());
            newId = susLoc.getUserId();
            if(distance <= 10) {
                // Within 10 meters, we save the id of the user to warn
                geolocationRepository.saveAndFlush(susLoc);
                usersToWarn.add(newId);
            }
        }
        return usersToWarn;
    }

    public void sendMailToCasContact(Set<String> usersToWarn){
        /*if(usersToWarn.size() != 0){
            StringBuilder str = new StringBuilder();
            for (String s : usersToWarn){
                str.append(s).append(",");
            }
            str.deleteCharAt(str.length() - 1);

            // Find mails thanks to id

            String[] idsUsers = Objects.requireNonNull(restTemplate.getForObject(urlGetIds, String[].class, idsRequest.toString()));
            String subject = "WARNING: Suspect case";
            String content = "Sir or Madam, \n\n" +
                    "You have previously entered a location being close to a person who has just declared himself positive for the Covid-19 virus.\n\n" +
                    "If you have any symptoms please do a PCR or antigen test. Be careful.\n\n Coviwad Team";
        }*/

    }

}
