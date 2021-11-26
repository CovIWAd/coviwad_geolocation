package fr.polytech.mtp.coviwad_geolocation.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "geolocations")
public class Geolocation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="geolocation_id")
    private Long id;

    @Column(name="user_id")
    private String userId;

    @Max(90)
    @Min(-90)
    private double latitude;

    @Max(180)
    @Min(-180)
    private double longitude;

    @Column(name="geolocation_date")
    private Date geolocationDate;

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getGeolocationDate() {
        return geolocationDate;
    }

    public String getUserId() {
        return userId;
    }

    public Geolocation() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setGeolocationDate(Date timestamp) {
        this.geolocationDate = timestamp;
    }

    public Geolocation(String userId, double latitude, double longitude, Date geolocationDate) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geolocationDate = geolocationDate;
    }

}
