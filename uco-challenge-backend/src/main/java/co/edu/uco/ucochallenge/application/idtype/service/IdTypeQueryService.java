package co.edu.uco.ucochallenge.application.idtype.service;

import java.util.List;

import co.edu.uco.ucochallenge.application.idtype.dto.IdTypeDTO;

public interface IdTypeQueryService {

        List<IdTypeDTO> findAll();
}
