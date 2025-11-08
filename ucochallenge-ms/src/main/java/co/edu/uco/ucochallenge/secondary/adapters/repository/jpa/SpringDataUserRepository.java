package co.edu.uco.ucochallenge.secondary.adapters.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

        Optional<UserEntity> findByIdTypeIdAndIdNumber(UUID idType, String idNumber);

        Optional<UserEntity> findByEmail(String email);

        Optional<UserEntity> findByMobileNumber(String mobileNumber);
}
