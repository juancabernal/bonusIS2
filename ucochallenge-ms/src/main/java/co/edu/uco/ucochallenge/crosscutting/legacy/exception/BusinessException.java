package co.edu.uco.ucochallenge.crosscutting.legacy.exception;

public class BusinessException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private final String code;

        public BusinessException(final String code) {
                super(code);
                this.code = code;
        }

        public BusinessException(final String code, final Throwable cause) {
                super(code, cause);
                this.code = code;
        }

        public String getCode() {
                return code;
        }
}
