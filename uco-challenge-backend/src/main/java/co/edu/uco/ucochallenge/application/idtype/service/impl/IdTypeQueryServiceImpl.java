package co.edu.uco.ucochallenge.application.idtype.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.idtype.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.application.idtype.service.IdTypeQueryService;
import co.edu.uco.ucochallenge.application.idtype.service.IdTypeMapper;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.IdTypeRepository;

@Service
public class IdTypeQueryServiceImpl implements IdTypeQueryService {

        private final IdTypeRepository repository;
        private final IdTypeMapper mapper;

        public IdTypeQueryServiceImpl(final IdTypeRepository repository, final IdTypeMapper mapper) {
                this.repository = repository;
                this.mapper = mapper;
        }

        @Override
        public List<IdTypeDTO> findAll() {
                return repository.findAll()
                                .stream()
                                .map(mapper::toDTO)
                                .toList();
        }
}
