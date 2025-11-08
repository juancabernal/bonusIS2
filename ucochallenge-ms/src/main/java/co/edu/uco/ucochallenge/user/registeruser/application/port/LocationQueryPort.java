package co.edu.uco.ucochallenge.user.registeruser.application.port;

import java.util.UUID;

public interface LocationQueryPort {

        boolean countryExists(UUID countryId);

        boolean departmentExists(UUID departmentId);

        boolean cityExists(UUID cityId);
}
