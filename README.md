# Coviwad - Geolocation

## Setup database for geolocation

Run docker-compose configuration:

`docker compose up`

After the services have started, the database should be available thanks to the command : `docker exec -it covid-postgres-geolocation psql -U postgres`
Then connect to the database '\c geolocation_covid'

**If there is no table named "documents" : copy/paste the content of `./sql/create-tables.sql`**

## Launch microservice

Run `bootRun` with the environment variables given in the documentation.

