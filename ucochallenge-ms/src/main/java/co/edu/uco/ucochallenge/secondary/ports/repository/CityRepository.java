package co.edu.uco.ucochallenge.secondary.ports.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {

        List<CityEntity> findByStateId(UUID stateId);
}
