package co.edu.uco.ucochallenge.application.user.confirm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.VerificationCodeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.VerificationCodeRepository;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private VerificationCodeRepository repository;

    private VerificationCodeService service;

    @BeforeEach
    void setUp() {
        service = new VerificationCodeService(repository);
    }

    @Test
    void generateCodeShouldReturnZeroPaddedNumericString() {
        String code = service.generateCode();

        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"), "El código debe ser numérico de seis dígitos");
    }

    @Test
    void findByContactShouldDelegateToRepository() {
        VerificationCodeEntity entity = new VerificationCodeEntity("user@example.com", "123456", LocalDateTime.now());
        when(repository.findByContact("user@example.com")).thenReturn(Optional.of(entity));

        Optional<VerificationCodeEntity> result = service.findByContact("user@example.com");

        assertTrue(result.isPresent());
        assertEquals(entity, result.orElseThrow());
        verify(repository).findByContact("user@example.com");
    }

    @Test
    void findByContactIgnoreCaseShouldDelegateToRepository() {
        VerificationCodeEntity entity = new VerificationCodeEntity("USER@example.com", "654321", LocalDateTime.now());
        when(repository.findByContactIgnoreCase("USER@example.com")).thenReturn(Optional.of(entity));

        Optional<VerificationCodeEntity> result = service.findByContactIgnoreCase("USER@example.com");

        assertTrue(result.isPresent());
        assertEquals(entity, result.orElseThrow());
        verify(repository).findByContactIgnoreCase("USER@example.com");
    }

    @Test
    void deleteOperationsShouldDelegateToRepository() {
        service.deleteByContact("user@example.com");
        service.deleteByContactIgnoreCase("user@example.com");

        verify(repository).deleteByContact("user@example.com");
        verify(repository).deleteByContactIgnoreCase("user@example.com");
    }

    @Test
    void saveShouldPersistEntityWithTrimmedContact() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
        VerificationCodeEntity storedEntity = new VerificationCodeEntity("user@example.com", "222333", expiration);
        when(repository.save(any(VerificationCodeEntity.class))).thenReturn(storedEntity);

        VerificationCodeEntity result = service.save("  user@example.com  ", "222333", expiration);

        ArgumentCaptor<VerificationCodeEntity> captor = ArgumentCaptor.forClass(VerificationCodeEntity.class);
        verify(repository).save(captor.capture());
        VerificationCodeEntity savedEntity = captor.getValue();

        assertEquals("user@example.com", savedEntity.getContact());
        assertEquals("222333", savedEntity.getCode());
        assertEquals(expiration, savedEntity.getExpiration());
        assertNotNull(result);
    }
}

