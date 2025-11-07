package co.edu.uco.ucochallenge.application.idtype.service.interactor.usecase.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.idtype.service.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.application.idtype.service.interactor.usecase.IdTypeQueryService;
import co.edu.uco.ucochallenge.application.idtype.service.mapper.IdTypeMapper;
import co.edu.uco.ucochallenge.infrastructure.secondary.ports.repository.IdTypeRepository;

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
