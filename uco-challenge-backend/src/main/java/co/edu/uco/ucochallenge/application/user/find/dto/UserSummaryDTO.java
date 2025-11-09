package co.edu.uco.ucochallenge.application.user.find.dto;

import java.util.UUID;

public class UserSummaryDTO {

        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String mobileNumber;
        private Boolean emailConfirmed;
        private Boolean mobileNumberConfirmed;

        public UUID getId() {
                return id;
        }

        public void setId(final UUID id) {
                this.id = id;
        }

        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(final String firstName) {
                this.firstName = firstName;
        }

        public String getLastName() {
                return lastName;
        }

        public void setLastName(final String lastName) {
                this.lastName = lastName;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(final String email) {
                this.email = email;
        }

        public String getMobileNumber() {
                return mobileNumber;
        }

        public void setMobileNumber(final String mobileNumber) {
                this.mobileNumber = mobileNumber;
        }

        public Boolean getEmailConfirmed() {
                return emailConfirmed;
        }

        public void setEmailConfirmed(final Boolean emailConfirmed) {
                this.emailConfirmed = emailConfirmed;
        }

        public Boolean getMobileNumberConfirmed() {
                return mobileNumberConfirmed;
        }

        public void setMobileNumberConfirmed(final Boolean mobileNumberConfirmed) {
                this.mobileNumberConfirmed = mobileNumberConfirmed;
        }
}
