# Usa una imagen oficial de OpenJDK 21 como base
FROM openjdk:21-jdk-slim

# Establecer un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR desde el directorio target al contenedor
COPY target/uco-challenge-apigateway-0.0.1-SNAPSHOT.jar /app/uco-challenge-apigateway.jar

# Exponer el puerto 8082, que es el puerto donde está corriendo tu API Gateway
EXPOSE 8082

# Comando para ejecutar la aplicación dentro del contenedor
ENTRYPOINT ["java", "-jar", "uco-challenge-apigateway.jar"]
