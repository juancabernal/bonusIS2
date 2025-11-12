package co.edu.uco.ucochallenge.application.user.confirm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.uco.ucochallenge.application.user.confirm.service.SendVerificationCodeService;
import co.edu.uco.ucochallenge.application.user.confirm.service.VerificationCodeService;
import co.edu.uco.ucochallenge.crosscutting.ParamKeys;
import co.edu.uco.ucochallenge.crosscutting.dto.ParameterDTO;
import co.edu.uco.ucochallenge.crosscutting.exception.BusinessException;
import co.edu.uco.ucochallenge.crosscutting.exception.NotificationDeliveryException;
import co.edu.uco.ucochallenge.crosscutting.exception.NotFoundException;
import co.edu.uco.ucochallenge.domain.user.confirm.VerificationChannel;
import co.edu.uco.ucochallenge.infrastructure.secondary.cache.catalog.ParametersCatalogCache;
import co.edu.uco.ucochallenge.infrastructure.secondary.notification.NotificationContactConfirmationAdapter;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.UserEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.entity.VerificationCodeEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.persistence.jpa.repository.SpringDataUserRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SendVerificationCodeServiceTest {

    @Mock
    private SpringDataUserRepository userRepository;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private NotificationContactConfirmationAdapter notifier;

    @Mock
    private ParametersCatalogCache parametersCatalogCache;

    @InjectMocks
    private SendVerificationCodeService service;

    @Test
    void shouldSendVerificationCodeUsingEmailChannel() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity.Builder()
                .id(userId)
                .email(" Test@Example.com ")
                .mobileNumber("3001234567")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(verificationCodeService.generateCode()).thenReturn("987654");
        when(parametersCatalogCache.getParameter(ParamKeys.TOKEN_DURATION_MINUTES))
                .thenReturn(Mono.just(new ParameterDTO(ParamKeys.TOKEN_DURATION_MINUTES, " 10 ")));
        when(verificationCodeService.save(anyString(), anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> new VerificationCodeEntity(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));

        LocalDateTime before = LocalDateTime.now();
        service.sendVerificationCode(userId, VerificationChannel.EMAIL);
        LocalDateTime after = LocalDateTime.now();

        verify(verificationCodeService).deleteByContactIgnoreCase("test@example.com");

        ArgumentCaptor<String> contactCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> expirationCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(verificationCodeService).save(contactCaptor.capture(), codeCaptor.capture(), expirationCaptor.capture());

        assertEquals("test@example.com", contactCaptor.getValue());
        assertEquals("987654", codeCaptor.getValue());

        LocalDateTime expected = before.plusMinutes(10);
        Duration difference = Duration.between(expected, expirationCaptor.getValue()).abs();
        assertTrue(difference.toSeconds() < 5, "La expiración debe estar a ~10 minutos en el futuro");
        assertTrue(!expirationCaptor.getValue().isAfter(after.plusMinutes(10).plusSeconds(5)));

        verify(notifier).sendCodeForChannel("Test@Example.com", null, "email", "987654");
    }

    @Test
    void shouldFallbackToDefaultTtlAndNormalizeMobileContact() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity.Builder()
                .id(userId)
                .email("user@example.com")
                .mobileNumber(" 3007654321 ")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(verificationCodeService.generateCode()).thenReturn("123456");
        when(parametersCatalogCache.getParameter(ParamKeys.TOKEN_DURATION_MINUTES))
                .thenReturn(Mono.just(new ParameterDTO(ParamKeys.TOKEN_DURATION_MINUTES, "invalido")));
        when(verificationCodeService.save(anyString(), anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> new VerificationCodeEntity(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));

        LocalDateTime before = LocalDateTime.now();
        service.sendVerificationCode(userId, VerificationChannel.MOBILE);
        LocalDateTime after = LocalDateTime.now();

        verify(verificationCodeService).deleteByContactIgnoreCase("3007654321");

        ArgumentCaptor<LocalDateTime> expirationCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(verificationCodeService).save(anyString(), anyString(), expirationCaptor.capture());

        Duration difference = Duration.between(before.plusMinutes(15), expirationCaptor.getValue()).abs();
        assertTrue(difference.toSeconds() < 5, "Debe usar TTL por defecto de 15 minutos");
        assertTrue(!expirationCaptor.getValue().isAfter(after.plusMinutes(15).plusSeconds(5)));

        verify(notifier).sendCodeForChannel(null, "3007654321", "mobile", "123456");
    }

    @Test
    void shouldThrowNotFoundWhenUserIsMissing() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.sendVerificationCode(userId, VerificationChannel.EMAIL));
    }

    @Test
    void shouldThrowBusinessExceptionWhenContactMissingForChannel() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity.Builder()
                .id(userId)
                .email(null)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> service.sendVerificationCode(userId, VerificationChannel.EMAIL));
    }

    @Test
    void shouldPropagateNotificationDeliveryException() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity.Builder()
                .id(userId)
                .email("user@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(verificationCodeService.generateCode()).thenReturn("123456");
        when(parametersCatalogCache.getParameter(ParamKeys.TOKEN_DURATION_MINUTES))
                .thenReturn(Mono.empty());
        when(verificationCodeService.save(anyString(), anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> new VerificationCodeEntity(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));
        doThrow(new NotificationDeliveryException("notification.error"))
                .when(notifier)
                .sendCodeForChannel(any(), any(), anyString(), anyString());

        assertThrows(NotificationDeliveryException.class,
                () -> service.sendVerificationCode(userId, VerificationChannel.EMAIL));
    }

    @Test
    void shouldWrapUnexpectedNotifierError() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity.Builder()
                .id(userId)
                .email("user@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(verificationCodeService.generateCode()).thenReturn("123456");
        when(parametersCatalogCache.getParameter(ParamKeys.TOKEN_DURATION_MINUTES))
                .thenReturn(Mono.empty());
        when(verificationCodeService.save(anyString(), anyString(), any(LocalDateTime.class)))
                .thenAnswer(invocation -> new VerificationCodeEntity(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));
        doThrow(new RuntimeException("boom"))
                .when(notifier)
                .sendCodeForChannel(any(), any(), anyString(), anyString());

        assertThrows(NotificationDeliveryException.class,
                () -> service.sendVerificationCode(userId, VerificationChannel.EMAIL));
    }
}

