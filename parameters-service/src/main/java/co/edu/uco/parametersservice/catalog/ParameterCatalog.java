package co.edu.uco.parametersservice.catalog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ParameterCatalog {

    // ===== Claves de parámetros (centralizadas) =====
    public static final String FECHA_DEFECTO_MAXIMA                 = "FechaDefectoMaxima";
    public static final String CORREO_ADMINISTRADOR                 = "correoAdministrador";
    public static final String NUM_MAX_REINTENTOS_ENVIO_CORREO      = "numeroMaximoReintentosEnvioCorrec";
    public static final String DURACION_TOKEN_MINUTOS               = "duracionTokenMinutos";
    public static final String NUM_MAX_REINTENTOS_CONFIRMACION      = "numeroMaximoReintentosConfirmacion";

    private static final Map<String, Parameter> PARAMETERS = new HashMap<>();

    static {
        PARAMETERS.put(FECHA_DEFECTO_MAXIMA,            new Parameter(FECHA_DEFECTO_MAXIMA, "31/12/2500"));
        PARAMETERS.put(CORREO_ADMINISTRADOR,            new Parameter(CORREO_ADMINISTRADOR, "juanjosenarvaezmarin13092005@gmail.com"));
        PARAMETERS.put(NUM_MAX_REINTENTOS_ENVIO_CORREO, new Parameter(NUM_MAX_REINTENTOS_ENVIO_CORREO, "8"));
        // Sugeridos para tu lógica de verificación:
        PARAMETERS.put(DURACION_TOKEN_MINUTOS,          new Parameter(DURACION_TOKEN_MINUTOS, "5"));
        PARAMETERS.put(NUM_MAX_REINTENTOS_CONFIRMACION, new Parameter(NUM_MAX_REINTENTOS_CONFIRMACION, "3"));
    }

    private ParameterCatalog() {
        // utilidad; evitar instanciación
    }

    public static Parameter getParameterValue(final String key) {
        return PARAMETERS.get(Objects.requireNonNull(key, "key"));
    }

    public static void synchronizeParameterValue(final Parameter parameter) {
        Objects.requireNonNull(parameter, "parameter");
        Objects.requireNonNull(parameter.getKey(), "parameter.key");
        PARAMETERS.put(parameter.getKey(), parameter);
    }

    public static Parameter removeParameter(final String key) {
        return PARAMETERS.remove(Objects.requireNonNull(key, "key"));
    }

    public static Map<String, Parameter> getAllParameters() {
        return Collections.unmodifiableMap(PARAMETERS);
    }
}
