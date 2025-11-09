package co.edu.uco.ucochallenge.application.user.find.usecase;

import co.edu.uco.ucochallenge.application.common.usecase.UseCase;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterInputDomain;
import co.edu.uco.ucochallenge.domain.user.find.model.FindUsersByFilterResponseDomain;

public interface FindUsersByFilterUseCase
                extends UseCase<FindUsersByFilterInputDomain, FindUsersByFilterResponseDomain> {
}
