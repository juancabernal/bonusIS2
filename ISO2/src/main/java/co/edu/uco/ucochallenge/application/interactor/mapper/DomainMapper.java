package co.edu.uco.ucochallenge.application.interactor.mapper;

public interface DomainMapper<DTO, DOMAIN> {

        DOMAIN toDomain(DTO dto);

        DTO toDto(DOMAIN domain);
}
