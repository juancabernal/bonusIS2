package co.edu.uco.ucochallenge.application.common.interactor;

public interface Interactor<T, R> {
	R execute(T dto);

}
