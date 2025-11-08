package co.edu.uco.ucochallenge.idtype.application.interactor.usecase.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.idtype.application.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.idtype.application.interactor.usecase.IdTypeQueryService;
import co.edu.uco.ucochallenge.idtype.application.mapper.IdTypeMapper;
import co.edu.uco.ucochallenge.secondary.ports.repository.IdTypeRepository;

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
