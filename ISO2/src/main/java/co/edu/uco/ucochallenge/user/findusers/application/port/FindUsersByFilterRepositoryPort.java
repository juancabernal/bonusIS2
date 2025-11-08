package co.edu.uco.ucochallenge.user.findusers.application.port;

import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterResponseDomain;

public interface FindUsersByFilterRepositoryPort {

        FindUsersByFilterResponseDomain findAll(int page, int size);
}
