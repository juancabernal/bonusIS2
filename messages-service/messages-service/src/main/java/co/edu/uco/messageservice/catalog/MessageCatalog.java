package co.edu.uco.messageservice.catalog;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catálogo centralizado de mensajes del sistema.
 * Solo define mensajes; no mezcla parámetros ni configuración.
 */
public final class MessageCatalog {

    private static final Map<String, Message> MESSAGES = new ConcurrentHashMap<>();

    static {
        // =========================================================
        // ⚙️ EXCEPCIONES GENERALES
        // =========================================================
        register("exception.general.unexpected",
                "Unexpected exception occurred while processing the request.",
                "Ocurrió un error inesperado. Por favor, intente nuevamente más tarde.",
                "Error general del sistema al procesar la solicitud.");

        register("exception.general.technical",
                "Technical exception in backend service. Review stack trace for details.",
                "No se pudo procesar la solicitud por un error interno.",
                "Falla técnica al ejecutar la operación.");

        register("exception.general.validation",
                "Validation process failed for input data.",
                "Los datos ingresados no son válidos.",
                "Error de validación detectado.");

        // =========================================================
        // 👤 REGISTRO DE USUARIOS
        // =========================================================
        register("register.user.success",
                "User registered successfully in database.",
                "Usuario registrado correctamente.",
                "El proceso de registro finalizó con éxito.");

        // --- Validaciones obligatorias ---
        register("register.user.validation.idtype.required",
                "Missing required field: identification type.",
                "El tipo de identificación es obligatorio.",
                "No se especificó el tipo de documento.");

        register("register.user.validation.idnumber.required",
                "Missing required field: identification number.",
                "El número de identificación es obligatorio.",
                "Campo de identificación faltante.");

        register("register.user.validation.idnumber.invalid",
                "Invalid identification number format: non-numeric or too short.",
                "El número de identificación no es válido.",
                "Formato de identificación incorrecto.");

        register("register.user.validation.firstname.required",
                "Missing required field: first name.",
                "El primer nombre es obligatorio.",
                "Falta el nombre principal del usuario.");

        register("register.user.validation.lastname.required",
                "Missing required field: last name.",
                "El primer apellido es obligatorio.",
                "El apellido principal no fue ingresado.");

        register("register.user.validation.email.invalid",
                "Email format invalid. Expected standard RFC 5322 pattern.",
                "El formato del correo electrónico no es válido.",
                "El correo ingresado no cumple con el formato permitido.");

        register("register.user.validation.phone.invalid",
                "Phone number must contain exactly 10 digits.",
                "El número de teléfono debe contener exactamente 10 dígitos.",
                "El teléfono no cumple la longitud esperada.");

        // --- Reglas de negocio ---
        register("register.user.rule.duplicated",
                "Duplicate user detected with same ID type and number.",
                "Ya existe un usuario registrado con esta identificación.",
                "El usuario ya se encuentra en el sistema.");

        register("register.user.identification.duplicated",
                "Duplicate user detected with the same identification type and number.",
                "Ya existe un usuario registrado con el mismo tipo y número de identificación.",
                "El sistema detectó un usuario duplicado con los mismos datos de identificación.");

        register("register.user.email.duplicated",
                "Duplicate email detected while attempting to register a user.",
                "El correo electrónico ya está registrado.",
                "El usuario ingresó un correo que ya está en uso.");

        register("register.user.phone.duplicated",
                "Duplicate phone number detected while attempting to register a user.",
                "El número de teléfono ya está registrado.",
                "El usuario ingresó un número que ya está en uso.");

        // Consolidado si quieres lanzar un solo código en el use case
        register("register.user.duplicated",
                "Duplicated data detected while registering user.",
                "Ya existe un usuario registrado con datos proporcionados.",
                "Se detectó información duplicada durante el registro.");

        // =========================================================
        // 🔐 AUTENTICACIÓN
        // =========================================================
        register("auth.login.failed",
                "Invalid credentials provided during authentication.",
                "Usuario o contraseña incorrectos.",
                "Falló el intento de inicio de sesión.");

        register("auth.token.expired",
                "JWT token expired. Needs re-authentication.",
                "La sesión ha expirado, por favor inicie sesión nuevamente.",
                "Token expirado.");

        register("auth.access.denied",
                "Access denied for the current user role.",
                "No tiene permisos para acceder a este recurso.",
                "Acceso restringido según las políticas del sistema.");

        // =========================================================
        // 📩 MENSAJES DEL CATÁLOGO
        // =========================================================
        register("catalog.message.updated",
                "Message successfully updated in catalog map.",
                "Mensaje actualizado correctamente.",
                "Actualización del mensaje completada.");

        register("catalog.message.removed",
                "Message successfully removed from catalog map.",
                "Mensaje eliminado correctamente.",
                "Se eliminó la entrada del catálogo.");

        // =========================================================
        // ✅ VERIFICACIÓN DE CONTACTO (opcional, úsalo si ya implementaste la lógica)
        // =========================================================
        register("verification.code.notfound",
                "No active verification code found for the provided contact.",
                "No existe un código activo para confirmar.",
                "No se encontró un código de verificación.");

        register("verification.code.expired",
                "Verification code expired for the provided contact.",
                "El código de verificación expiró.",
                "El código ya no es válido por tiempo.");

        register("verification.code.invalid",
                "Verification code does not match the stored value.",
                "El código de verificación es incorrecto.",
                "El código no coincide.");

        register("verification.code.max.attempts",
                "Maximum number of confirmation attempts reached.",
                "Has superado el número máximo de intentos.",
                "Se superó el límite de intentos de confirmación.");
    }

    private MessageCatalog() { /* utility */ }

    /** Registra o reemplaza un mensaje. */
    private static void register(final String code, final String technical, final String user, final String general) {
        MESSAGES.put(code, new Message(code, technical, user, general));
    }

    /** Obtiene un mensaje por código (o null si no existe). */
    public static Message get(final String code) {
        return MESSAGES.get(code);
    }

    /** Vista inmutable de todos los mensajes. */
    public static Map<String, Message> getAll() {
        return Collections.unmodifiableMap(MESSAGES);
    }

    /** Inserta o actualiza un mensaje. */
    public static void upsert(final Message message) {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(message.getCode(), "message.code");
        MESSAGES.put(message.getCode(), message);
    }

    /** Elimina un mensaje por código (retorna el eliminado o null). */
    public static Message remove(final String code) {
        return MESSAGES.remove(code);
    }
}
