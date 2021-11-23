CREATE TABLE IF NOT EXISTS geolocations (
    geolocation_id serial PRIMARY KEY NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    geolocation_date timestamp WITHOUT TIME ZONE,
    user_id varchar(36) NOT NULL
);