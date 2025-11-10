package snippet;

public class Snippet {
	# Perfil docker para el API Gateway
	server.port=8082
	spring.application.name=uco-challenge-apigateway
	
	# Ruta principal: /api/** -> backend en tu PC (8083)
	spring.cloud.gateway.routes[0].id=backend-api
	spring.cloud.gateway.routes[0].uri=http://host.docker.internal:8083
	spring.cloud.gateway.routes[0].predicates[0]=Path=/api/**
	spring.cloud.gateway.routes[0].filters[0]=RewritePath=/api/(?<segment>.*),/uco-challenge/api/v1/${segment}
	
}

