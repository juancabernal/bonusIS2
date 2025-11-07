package co.edu.uco.ucochallenge.crosscutting.legacy.exception;

public class NotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public NotFoundException(final String message) {
                super(message);
        }
}
