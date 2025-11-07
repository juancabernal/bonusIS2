package co.edu.uco.ucochallenge.application.idtype.service.interactor.usecase;

import java.util.List;

import co.edu.uco.ucochallenge.application.idtype.service.dto.IdTypeDTO;

public interface IdTypeQueryService {

        List<IdTypeDTO> findAll();
}
