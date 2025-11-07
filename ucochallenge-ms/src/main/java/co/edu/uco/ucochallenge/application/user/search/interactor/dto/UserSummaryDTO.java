package co.edu.uco.ucochallenge.application.user.search.interactor.dto;

import java.util.UUID;

public class UserSummaryDTO {

        private UUID id;
        private UUID idTypeId;
        private String idNumber;
        private String firstName;
        private String secondName;
        private String lastName;
        private String secondSurname;
        private UUID homeCityId;
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

        public UUID getIdTypeId() {
                return idTypeId;
        }

        public void setIdTypeId(final UUID idTypeId) {
                this.idTypeId = idTypeId;
        }

        public String getIdNumber() {
                return idNumber;
        }

        public void setIdNumber(final String idNumber) {
                this.idNumber = idNumber;
        }

        public String getFirstName() {
                return firstName;
        }

        public void setFirstName(final String firstName) {
                this.firstName = firstName;
        }

        public String getSecondName() {
                return secondName;
        }

        public void setSecondName(final String secondName) {
                this.secondName = secondName;
        }

        public String getLastName() {
                return lastName;
        }

        public void setLastName(final String lastName) {
                this.lastName = lastName;
        }

        public String getSecondSurname() {
                return secondSurname;
        }

        public void setSecondSurname(final String secondSurname) {
                this.secondSurname = secondSurname;
        }

        public UUID getHomeCityId() {
                return homeCityId;
        }

        public void setHomeCityId(final UUID homeCityId) {
                this.homeCityId = homeCityId;
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
