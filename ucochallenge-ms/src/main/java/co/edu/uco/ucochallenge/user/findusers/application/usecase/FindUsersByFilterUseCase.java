package co.edu.uco.ucochallenge.user.findusers.application.usecase;

import co.edu.uco.ucochallenge.application.interactor.usecase.UseCase;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterInputDomain;
import co.edu.uco.ucochallenge.user.findusers.application.usecase.domain.FindUsersByFilterResponseDomain;

public interface FindUsersByFilterUseCase
                extends UseCase<FindUsersByFilterInputDomain, FindUsersByFilterResponseDomain> {
}
