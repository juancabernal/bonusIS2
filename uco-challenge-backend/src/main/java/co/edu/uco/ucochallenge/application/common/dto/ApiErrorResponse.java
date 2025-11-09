package co.edu.uco.ucochallenge.application.common.dto;

import co.edu.uco.ucochallenge.crosscutting.helper.TextHelper;

public final class ApiErrorResponse extends Response<ApiErrorResponse.ErrorData> {

        private ApiErrorResponse(final boolean returnData, final ErrorData data) {
                super(returnData, data);
        }

        public static ApiErrorResponse businessError(final String message) {
                return new ApiErrorResponse(true, new ErrorData("BUSINESS_ERROR", message));
        }

        public static ApiErrorResponse validationError(final String message) {
                return new ApiErrorResponse(true, new ErrorData("VALIDATION_ERROR", message));
        }

        public static ApiErrorResponse unexpectedError(final String message) {
                return new ApiErrorResponse(true, new ErrorData("UNEXPECTED_ERROR", message));
        }

        public record ErrorData(String code, String message) {

                public ErrorData {
                        code = TextHelper.getDefaultWithTrim(code);
                        message = TextHelper.getDefaultWithTrim(message);
                }
        }
        //l
}
