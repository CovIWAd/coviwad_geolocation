package fr.polytech.mtp.coviwad_geolocation.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserPositive {

    @NotNull
    private final String userId;

    @Min(0)
    private final Long timestamp;

    public UserPositive(String userId, Long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
