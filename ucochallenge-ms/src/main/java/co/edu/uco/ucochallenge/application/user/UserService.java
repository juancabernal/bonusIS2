package co.edu.uco.ucochallenge.application.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.adapters.repository.jpa.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity registerUser(final UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("Datos de usuario obligatorios");
        }

        final String email = Optional.ofNullable(user.getEmail()).map(String::trim).orElse("");
        if (!email.isEmpty() && userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        final UserEntity normalizedUser = buildNormalizedUser(user, email);
        return userRepository.save(normalizedUser);
    }

    private UserEntity buildNormalizedUser(final UserEntity user, final String email) {
        final IdTypeEntity idType = Optional.ofNullable(user.getIdType())
                .orElseGet(() -> new IdTypeEntity.Builder().build());
        final CityEntity homeCity = Optional.ofNullable(user.getHomeCity())
                .orElseGet(() -> new CityEntity.Builder().build());

        final UserEntity.Builder builder = new UserEntity.Builder()
                .id(UUID.randomUUID())
                .idType(idType)
                .idNumber(Optional.ofNullable(user.getIdNumber()).orElse(""))
                .firstName(Optional.ofNullable(user.getFirstName()).orElse(""))
                .secondName(Optional.ofNullable(user.getSecondName()).orElse(""))
                .firstSurname(Optional.ofNullable(user.getFirstSurname()).orElse(""))
                .secondSurname(Optional.ofNullable(user.getSecondSurname()).orElse(""))
                .homeCity(homeCity)
                .email(email)
                .mobileNumber(Optional.ofNullable(user.getMobileNumber()).orElse(""));

        if (user.isEmailConfirmed()) {
            builder.emailConfirmed(true);
        }
        if (user.isMobileNumberConfirmed()) {
            builder.mobileNumberConfirmed(true);
        }

        return builder.build();
    }
}
