package co.edu.uco.ucochallenge.application.idtype.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uco.ucochallenge.application.idtype.dto.IdTypeDTO;
import co.edu.uco.ucochallenge.application.idtype.service.IdTypeMapper;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.IdTypeRepository;

@ExtendWith(MockitoExtension.class)
class IdTypeQueryServiceImplTest {

    @Mock
    private IdTypeRepository repository;

    @Mock
    private IdTypeMapper mapper;

    @InjectMocks
    private IdTypeQueryServiceImpl service;

    @Test
    void findAllShouldMapEntitiesToDtos() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        IdTypeEntity firstEntity = new IdTypeEntity.Builder()
                .id(firstId)
                .name("Cédula")
                .build();
        IdTypeEntity secondEntity = new IdTypeEntity.Builder()
                .id(secondId)
                .name("Pasaporte")
                .build();

        when(repository.findAll()).thenReturn(List.of(firstEntity, secondEntity));
        when(mapper.toDTO(firstEntity)).thenReturn(new IdTypeDTO(firstId, "Cédula"));
        when(mapper.toDTO(secondEntity)).thenReturn(new IdTypeDTO(secondId, "Pasaporte"));

        List<IdTypeDTO> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Cédula", result.get(0).name());
        assertEquals("Pasaporte", result.get(1).name());
        verify(mapper).toDTO(firstEntity);
        verify(mapper).toDTO(secondEntity);
    }

    @Test
    void findAllShouldReturnEmptyListWhenRepositoryIsEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        List<IdTypeDTO> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }
}

