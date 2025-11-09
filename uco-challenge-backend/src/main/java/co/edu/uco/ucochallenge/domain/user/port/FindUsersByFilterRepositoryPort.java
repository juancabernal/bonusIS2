package co.edu.uco.ucochallenge.domain.user.port;

import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterResponseDomain;

public interface FindUsersByFilterRepositoryPort {

        FindUsersByFilterResponseDomain findAll(int page, int size);
}
