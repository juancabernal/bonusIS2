package co.edu.uco.ucochallenge.domain.user.search.model;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import co.edu.uco.ucochallenge.crosscutting.legacy.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscutting.legacy.helper.UUIDHelper;

@JsonDeserialize(builder = UserSearchSummaryDomainModel.Builder.class)
public class UserSearchSummaryDomainModel {

        private final UUID id;
        private final UUID idType;
        private final String idNumber;
        private final String firstName;
        private final String secondName;
        private final String firstSurname;
        private final String secondSurname;
        private final UUID homeCity;
        private final String email;
        private final String mobileNumber;
        private final boolean emailConfirmed;
        private final boolean mobileNumberConfirmed;

        private UserSearchSummaryDomainModel(final Builder builder) {
                this.id = UUIDHelper.getDefault(builder.id);
                this.idType = UUIDHelper.getDefault(builder.idType);
                this.idNumber = TextHelper.getDefaultWithTrim(builder.idNumber);
                this.firstName = TextHelper.getDefaultWithTrim(builder.firstName);
                this.secondName = TextHelper.getDefaultWithTrim(builder.secondName);
                this.firstSurname = TextHelper.getDefaultWithTrim(builder.firstSurname);
                this.secondSurname = TextHelper.getDefaultWithTrim(builder.secondSurname);
                this.homeCity = UUIDHelper.getDefault(builder.homeCity);
                this.email = TextHelper.getDefaultWithTrim(builder.email);
                this.mobileNumber = TextHelper.getDefaultWithTrim(builder.mobileNumber);
                this.emailConfirmed = builder.emailConfirmed;
                this.mobileNumberConfirmed = builder.mobileNumberConfirmed;
        }

        public static Builder builder() {
                return new Builder();
        }

        public UUID getId() {
                return id;
        }

        public UUID getIdType() {
                return idType;
        }

        public String getIdNumber() {
                return idNumber;
        }

        public String getFirstName() {
                return firstName;
        }

        public String getSecondName() {
                return secondName;
        }

        public String getFirstSurname() {
                return firstSurname;
        }

        public String getSecondSurname() {
                return secondSurname;
        }

        public UUID getHomeCity() {
                return homeCity;
        }

        public String getEmail() {
                return email;
        }

        public String getMobileNumber() {
                return mobileNumber;
        }

        public boolean isEmailConfirmed() {
                return emailConfirmed;
        }

        public boolean isMobileNumberConfirmed() {
                return mobileNumberConfirmed;
        }

        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {

                private UUID id = UUIDHelper.getDefault();
                private UUID idType = UUIDHelper.getDefault();
                private String idNumber = TextHelper.getDefault();
                private String firstName = TextHelper.getDefault();
                private String secondName = TextHelper.getDefault();
                private String firstSurname = TextHelper.getDefault();
                private String secondSurname = TextHelper.getDefault();
                private UUID homeCity = UUIDHelper.getDefault();
                private String email = TextHelper.getDefault();
                private String mobileNumber = TextHelper.getDefault();
                private boolean emailConfirmed;
                private boolean mobileNumberConfirmed;

                public Builder id(final UUID id) {
                        this.id = id;
                        return this;
                }

                public Builder idType(final UUID idType) {
                        this.idType = idType;
                        return this;
                }

                public Builder idNumber(final String idNumber) {
                        this.idNumber = idNumber;
                        return this;
                }

                public Builder firstName(final String firstName) {
                        this.firstName = firstName;
                        return this;
                }

                public Builder secondName(final String secondName) {
                        this.secondName = secondName;
                        return this;
                }

                public Builder firstSurname(final String firstSurname) {
                        this.firstSurname = firstSurname;
                        return this;
                }

                public Builder secondSurname(final String secondSurname) {
                        this.secondSurname = secondSurname;
                        return this;
                }

                public Builder homeCity(final UUID homeCity) {
                        this.homeCity = homeCity;
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

                public Builder emailConfirmed(final boolean emailConfirmed) {
                        this.emailConfirmed = emailConfirmed;
                        return this;
                }

                public Builder mobileNumberConfirmed(final boolean mobileNumberConfirmed) {
                        this.mobileNumberConfirmed = mobileNumberConfirmed;
                        return this;
                }

                public UserSearchSummaryDomainModel build() {
                        return new UserSearchSummaryDomainModel(this);
                }
        }
}
