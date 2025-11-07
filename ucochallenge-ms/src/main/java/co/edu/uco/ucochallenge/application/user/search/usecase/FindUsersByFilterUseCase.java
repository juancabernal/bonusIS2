package co.edu.uco.ucochallenge.application.user.search.usecase;

import co.edu.uco.ucochallenge.application.interactor.usecase.UseCase;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchFilterDomainModel;
import co.edu.uco.ucochallenge.domain.user.search.model.UserSearchResultDomainModel;

public interface FindUsersByFilterUseCase
                extends UseCase<UserSearchFilterDomainModel, UserSearchResultDomainModel> {
}
