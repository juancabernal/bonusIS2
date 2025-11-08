package co.edu.uco.ucochallenge.application.common.usecase;

public interface UseCase<D, R> {
	R execute(D domain);

}
