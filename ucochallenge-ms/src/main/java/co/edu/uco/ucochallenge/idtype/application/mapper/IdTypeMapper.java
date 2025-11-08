package co.edu.uco.ucochallenge.idtype.application.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.idtype.application.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;

@Component
public class IdTypeMapper {

        public IdTypeDTO toDTO(final IdTypeEntity entity) {
                return new IdTypeDTO(entity.getId(), entity.getName());
        }
}
