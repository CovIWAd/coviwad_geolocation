package fr.polytech.mtp.coviwad_geolocation.services;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.repositories.GeolocationRepository;
import fr.polytech.mtp.coviwad_geolocation.utils.GeolocationUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

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

    public Map<String, List<Geolocation>> retrieveGeolocationsDaysKafka(ConsumerRecords<String,Geolocation> geolocations, int days, String idUserCovid) {
        long currentDate = new Date().getTime();
        //location to know if it's the person covided or potential contact
        Geolocation geolocationNotDetermined;
        long locationDate;

        long diffInMs;
        long diffInDays;

        List<Geolocation> geolocationsCovid = new ArrayList<>();
        List<Geolocation> geolocationsPotentialContact = new ArrayList<>();

        for (ConsumerRecord<String, Geolocation> location : geolocations) {
            geolocationNotDetermined = location.value();

            locationDate = geolocationNotDetermined.getGeolocationDate() == null ?
                    location.timestamp() :
                    geolocationNotDetermined.getGeolocationDate().getTime();

            diffInMs = Math.abs(currentDate - locationDate);
            diffInDays = TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);

            if(diffInDays <= days) {
                if(geolocationNotDetermined.getUserId().equals(idUserCovid)) {
                    // location of the person covided
                    geolocationsCovid.add(geolocationNotDetermined);
                } else {
                    geolocationNotDetermined.setGeolocationDate(new Date(locationDate));
                    // location of a potential contact
                    geolocationsPotentialContact.add(geolocationNotDetermined);
                }
            }
        }

        Map<String,List<Geolocation>> map =new HashMap();
        map.put("geolocCovid",geolocationsCovid);
        map.put("geolocContact",geolocationsPotentialContact);
        return map;
    }

    public Set<String> getUsersPotentialCovid (ConsumerFactory<String, Geolocation> geolocationConsumerFactory, GeolocationRepository geolocationRepository, String userCovid){
        Consumer<String,Geolocation> consumer = geolocationConsumerFactory.createConsumer();
        ConsumerRecords<String, Geolocation> geolocations = kafkaGetGeolocations(consumer);

        Map<String, List<Geolocation>> map = retrieveGeolocationsDaysKafka(geolocations,5, userCovid);

        List<Geolocation> geolocationsCovid = map.get("geolocCovid");

        List<Geolocation> geolocationsWithin5Days = retrieveGeolocationsDaysKafka(geolocations,5);

        Set<String> usersToWarn = new HashSet<>();
        String newId;
        double distance;
        for(Geolocation susLoc: geolocationsContact) {
            for(Geolocation covLoc: geolocationsCovid){
                distance = GeolocationUtils.distanceBetween2Points(
                        susLoc.getLatitude(),
                        susLoc.getLongitude(),
                        covLoc.getLatitude(),
                        covLoc.getLongitude());
                newId = susLoc.getUserId();
                if(distance <= 10) {
                    //we save all contact geolocations in BD
                    geolocationRepository.saveAndFlush(susLoc);
                    //array of people to send mail to warn them they are contact case
                    usersToWarn.add(newId);
                }
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
