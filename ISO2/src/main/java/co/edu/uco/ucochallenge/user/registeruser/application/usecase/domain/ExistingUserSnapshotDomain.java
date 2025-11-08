package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;

public class ExistingUserSnapshotDomain {

        private final UUID id;
        private final String firstName;
        private final String firstSurname;
        private final String email;
        private final String mobileNumber;

        private ExistingUserSnapshotDomain(final Builder builder) {
                this.id = UUIDHelper.getDefault(builder.id);
                this.firstName = TextHelper.getDefaultWithTrim(builder.firstName);
                this.firstSurname = TextHelper.getDefaultWithTrim(builder.firstSurname);
                this.email = TextHelper.getDefaultWithTrim(builder.email);
                this.mobileNumber = TextHelper.getDefaultWithTrim(builder.mobileNumber);
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

        public static final class Builder {

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

                public ExistingUserSnapshotDomain build() {
                        return new ExistingUserSnapshotDomain(this);
                }
        }
}
