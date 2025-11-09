package co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.IdTypeEntity;

@Repository
public interface IdTypeRepository extends JpaRepository<IdTypeEntity, UUID> {

        Optional<IdTypeEntity> findByName(String name);

        boolean existsByNameIgnoreCase(String name);
}
