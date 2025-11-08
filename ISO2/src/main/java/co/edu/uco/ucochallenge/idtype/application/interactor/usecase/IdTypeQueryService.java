package co.edu.uco.ucochallenge.idtype.application.interactor.usecase;

import java.util.List;

import co.edu.uco.ucochallenge.idtype.application.dto.IdTypeDTO;

public interface IdTypeQueryService {

        List<IdTypeDTO> findAll();
}
