# Usa OpenJDK 21 como base
FROM openjdk:21-jdk-slim

# Directorio de trabajo
WORKDIR /app

# Copia el jar que se construye con Maven. Cambia el nombre según cada módulo
COPY target/messages-service 0.0.1-SNAPSHOT.jar /app/app.jar

# Copia el agente OTel descargado previamente
COPY opentelemetry-javaagent.jar /app/

# Configura la variable JAVA_TOOL_OPTIONS para usar el agente (esto es lo que indica la documentación oficial:contentReference[oaicite:1]{index=1})
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

# Configura el endpoint de exportación OTLP (hacia el colector) si usas docker-compose
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317

# Expón el puerto interno (cambia según cada servicio)
EXPOSE 8081

# Ejecuta la aplicación
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /app/app.jar"]
