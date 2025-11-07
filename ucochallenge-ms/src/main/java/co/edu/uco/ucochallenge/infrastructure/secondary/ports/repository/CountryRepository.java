package co.edu.uco.ucochallenge.infrastructure.secondary.ports.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.CountryEntity;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
}
