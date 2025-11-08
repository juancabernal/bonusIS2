package co.edu.uco.ucochallenge.location.application.dto;

import java.util.UUID;

public class CityDTO {

        private UUID id;
        private String name;

        public UUID getId() {
                return id;
        }

        public void setId(final UUID id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(final String name) {
                this.name = name;
        }
}
