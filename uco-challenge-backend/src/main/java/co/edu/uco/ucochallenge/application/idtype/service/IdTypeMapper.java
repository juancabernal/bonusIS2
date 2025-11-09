package co.edu.uco.ucochallenge.application.idtype.service;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.idtype.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.IdTypeEntity;

@Component
public class IdTypeMapper {

        public IdTypeDTO toDTO(final IdTypeEntity entity) {
                return new IdTypeDTO(entity.getId(), entity.getName());
        }
}
