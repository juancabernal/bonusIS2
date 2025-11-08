package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.crosscuting.notification.Notification;
import co.edu.uco.ucochallenge.crosscuting.notification.SelfValidating;

public class RegisterUserDomain implements SelfValidating {

        private static final String ID_NUMBER_REQUIRED_CODE = "register.user.validation.idnumber.required";
        private static final String FIRST_NAME_REQUIRED_CODE = "register.user.validation.firstname.required";
        private static final String LAST_NAME_REQUIRED_CODE = "register.user.validation.lastname.required";
        private static final String EMAIL_REQUIRED_CODE = "register.user.validation.email.required";
        private static final String COUNTRY_REQUIRED_CODE = "register.user.validation.country.required";
        private static final String DEPARTMENT_REQUIRED_CODE = "register.user.validation.department.required";
        private static final String CITY_REQUIRED_CODE = "register.user.validation.city.required";

        private UUID id;
        private UUID idType;
        private String idTypeName;
        private String idNumber;
        private String firstName;
        private String secondName;
        private String firstSurname;
        private String secondSurname;
        private UUID countryId;
        private UUID departmentId;
        private UUID homeCity;
        private String email;
        private String mobileNumber;
        private boolean emailConfirmed;
        private boolean mobileNumberConfirmed;

        private RegisterUserDomain(final Builder builder) {
                setId(builder.id);
                setIdType(builder.idType);
                setIdTypeName(builder.idTypeName);
                setIdNumber(builder.idNumber);
                setFirstName(builder.firstName);
                setSecondName(builder.secondName);
                setFirstSurname(builder.firstSurname);
                setSecondSurname(builder.secondSurname);
                setCountryId(builder.countryId);
                setDepartmentId(builder.departmentId);
                setHomeCity(builder.homeCity);
                setEmail(builder.email);
                setMobileNumber(builder.mobileNumber);
                setEmailConfirmed(builder.emailConfirmed);
                setMobileNumberConfirmed(builder.mobileNumberConfirmed);
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

        public String getIdTypeName() {
                return idTypeName;
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

        public UUID getCountryId() {
                return countryId;
        }

        public UUID getDepartmentId() {
                return departmentId;
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

        public boolean hasEmail() {
                return !TextHelper.isEmpty(email);
        }

        public boolean hasMobileNumber() {
                return !TextHelper.isEmpty(mobileNumber);
        }

        @Override
        public Notification validate() {
                final var notification = Notification.create();

                if (TextHelper.isEmpty(idNumber)) {
                        notification.addError(ID_NUMBER_REQUIRED_CODE, null);
                }

                if (TextHelper.isEmpty(firstName)) {
                        notification.addError(FIRST_NAME_REQUIRED_CODE, null);
                }

                if (TextHelper.isEmpty(firstSurname)) {
                        notification.addError(LAST_NAME_REQUIRED_CODE, null);
                }

                if (TextHelper.isEmpty(email)) {
                        notification.addError(EMAIL_REQUIRED_CODE, null);
                }

                if (isMissing(countryId)) {
                        notification.addError(COUNTRY_REQUIRED_CODE, null);
                }

                if (isMissing(departmentId)) {
                        notification.addError(DEPARTMENT_REQUIRED_CODE, null);
                }

                if (isMissing(homeCity)) {
                        notification.addError(CITY_REQUIRED_CODE, null);
                }

                return notification;
        }

        public void updateId(final UUID newId) {
                setId(newId);
        }

        public void updateIdType(final UUID newIdType) {
                setIdType(newIdType);
        }

        public void markEmailConfirmed() {
                        setEmailConfirmed(true);
        }

        public void markMobileNumberConfirmed() {
                        setMobileNumberConfirmed(true);
        }

        private void setId(final UUID id) {
                this.id = UUIDHelper.getDefault(id);
        }

        private void setIdType(final UUID idType) {
                this.idType = UUIDHelper.getDefault(idType);
        }

        private void setIdTypeName(final String idTypeName) {
                this.idTypeName = TextHelper.isEmpty(idTypeName) ? null : TextHelper.getDefaultWithTrim(idTypeName);
        }

        private void setIdNumber(final String idNumber) {
                this.idNumber = TextHelper.getDefaultWithTrim(idNumber);
        }

        private void setFirstName(final String firstName) {
                this.firstName = TextHelper.getDefaultWithTrim(firstName);
        }

        private void setSecondName(final String secondName) {
                this.secondName = TextHelper.getDefaultWithTrim(secondName);
        }

        private void setFirstSurname(final String firstSurname) {
                this.firstSurname = TextHelper.getDefaultWithTrim(firstSurname);
        }

        private void setSecondSurname(final String secondSurname) {
                this.secondSurname = TextHelper.getDefaultWithTrim(secondSurname);
        }

        private void setCountryId(final UUID countryId) {
                this.countryId = UUIDHelper.getDefault(countryId);
        }

        private void setDepartmentId(final UUID departmentId) {
                this.departmentId = UUIDHelper.getDefault(departmentId);
        }

        private void setHomeCity(final UUID homeCity) {
                this.homeCity = UUIDHelper.getDefault(homeCity);
        }

        private void setEmail(final String email) {
                this.email = TextHelper.getDefaultWithTrim(email);
        }

        private void setMobileNumber(final String mobileNumber) {
                this.mobileNumber = TextHelper.getDefaultWithTrim(mobileNumber);
        }

        private boolean isMissing(final UUID value) {
                return value == null || UUIDHelper.getDefault().equals(value);
        }

        private void setEmailConfirmed(final boolean emailConfirmed) {
                this.emailConfirmed = emailConfirmed;
        }

        private void setMobileNumberConfirmed(final boolean mobileNumberConfirmed) {
                this.mobileNumberConfirmed = mobileNumberConfirmed;
        }

        public static final class Builder {
                private UUID id = UUIDHelper.getDefault();
                private UUID idType = UUIDHelper.getDefault();
                private String idTypeName = TextHelper.getDefault();
                private String idNumber = TextHelper.getDefault();
                private String firstName = TextHelper.getDefault();
                private String secondName = TextHelper.getDefault();
                private String firstSurname = TextHelper.getDefault();
                private String secondSurname = TextHelper.getDefault();
                private UUID countryId = UUIDHelper.getDefault();
                private UUID departmentId = UUIDHelper.getDefault();
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

                public Builder idTypeName(final String idTypeName) {
                        this.idTypeName = idTypeName;
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

                public Builder countryId(final UUID countryId) {
                        this.countryId = countryId;
                        return this;
                }

                public Builder departmentId(final UUID departmentId) {
                        this.departmentId = departmentId;
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

                public RegisterUserDomain build() {
                        return new RegisterUserDomain(this);
                }
        }
}
