package co.edu.uco.ucochallenge.application.user.find.dto;

import co.edu.uco.ucochallenge.crosscutting.helper.NumberHelper;

public record FindUsersByFilterInputDTO(Integer page, Integer size) {

        private static final int DEFAULT_PAGE = 0;
        private static final int DEFAULT_SIZE = 10;

        public static FindUsersByFilterInputDTO normalize(final Integer page, final Integer size) {
                var sanitizedPage = NumberHelper.ensureMinimum(NumberHelper.getDefault(page, DEFAULT_PAGE), 0, DEFAULT_PAGE);
                var sanitizedSize = NumberHelper.ensureRange(NumberHelper.getDefault(size, DEFAULT_SIZE), 1, 100, DEFAULT_SIZE);
                return new FindUsersByFilterInputDTO(sanitizedPage, sanitizedSize);
        }
}
