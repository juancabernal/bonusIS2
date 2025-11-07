package co.edu.uco.ucochallenge.application.idtype.service.mapper;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.idtype.service.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.IdTypeEntity;

@Component
public class IdTypeMapper {

        public IdTypeDTO toDTO(final IdTypeEntity entity) {
                return new IdTypeDTO(entity.getId(), entity.getName());
        }
}
