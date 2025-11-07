package co.edu.uco.ucochallenge.domain.user.registration.model;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;

public class UserRegistrationExistingUserSnapshotDomainModel {

        private UUID id;
        private String firstName;
        private String firstSurname;
        private String email;
        private String mobileNumber;

        private UserRegistrationExistingUserSnapshotDomainModel(final Builder builder) {
                setId(builder.id);
                setFirstName(builder.firstName);
                setFirstSurname(builder.firstSurname);
                setEmail(builder.email);
                setMobileNumber(builder.mobileNumber);
        }

        public static Builder builder() {
                return new Builder();
        }

        public UUID getId() {
                return id;
        }

        public String getFirstName() {
                return firstName;
        }

        public String getFirstSurname() {
                return firstSurname;
        }

        public String getEmail() {
                return email;
        }

        public String getMobileNumber() {
                return mobileNumber;
        }

        private void setId(final UUID id) {
                this.id = UUIDHelper.getDefault(id);
        }

        private void setFirstName(final String firstName) {
                this.firstName = TextHelper.getDefaultWithTrim(firstName);
        }

        private void setFirstSurname(final String firstSurname) {
                this.firstSurname = TextHelper.getDefaultWithTrim(firstSurname);
        }

        private void setEmail(final String email) {
                this.email = TextHelper.getDefaultWithTrim(email);
        }

        private void setMobileNumber(final String mobileNumber) {
                this.mobileNumber = TextHelper.getDefaultWithTrim(mobileNumber);
        }

        public static class Builder {

                private UUID id = UUIDHelper.getDefault();
                private String firstName = TextHelper.getDefault();
                private String firstSurname = TextHelper.getDefault();
                private String email = TextHelper.getDefault();
                private String mobileNumber = TextHelper.getDefault();

                public Builder id(final UUID id) {
                        this.id = id;
                        return this;
                }

                public Builder firstName(final String firstName) {
                        this.firstName = firstName;
                        return this;
                }

                public Builder firstSurname(final String firstSurname) {
                        this.firstSurname = firstSurname;
                        return this;
                }

                public Builder email(final String email) {
                        this.email = email;
                        return this;
                }

                public Builder mobileNumber(final String mobileNumber) {
                        this.mobileNumber = mobileNumber;
                        return this;
                }

                public UserRegistrationExistingUserSnapshotDomainModel build() {
                        return new UserRegistrationExistingUserSnapshotDomainModel(this);
                }
        }
}
