package co.edu.uco.ucochallenge.domain.user.find.model;

import co.edu.uco.ucochallenge.crosscutting.helper.NumberHelper;
import co.edu.uco.ucochallenge.domain.common.notification.Notification;
import co.edu.uco.ucochallenge.domain.common.notification.SelfValidating;

public class FindUsersByFilterInputDomain implements SelfValidating {

        private static final int DEFAULT_PAGE = 0;
        private static final int DEFAULT_SIZE = 10;
        private static final String PAGE_NEGATIVE_CODE = "FIND_USERS_PAGE_NEGATIVE";
        private static final String SIZE_RANGE_CODE = "FIND_USERS_SIZE_RANGE";

        private final int page;
        private final int size;

        private FindUsersByFilterInputDomain(final Builder builder) {
                this.page = sanitizePage(builder.page);
                this.size = sanitizeSize(builder.size);
        }

        public static Builder builder() {
                return new Builder();
        }

        public int getPage() {
                return page;
        }

        public int getSize() {
                return size;
        }

        @Override
        public Notification validate() {
                final var notification = Notification.create();
                if (page < 0) {
                        notification.addError(PAGE_NEGATIVE_CODE, "Page index must be greater or equal to zero");
                }

                if (size < 1 || size > 100) {
                        notification.addError(SIZE_RANGE_CODE, "Page size must be between 1 and 100");
                }

                return notification;
        }

        private int sanitizePage(final Integer page) {
                final var defaultValue = DEFAULT_PAGE;
                final var sanitizedPage = NumberHelper.getDefault(page, defaultValue);
                return Math.max(0, sanitizedPage);
        }

        private int sanitizeSize(final Integer size) {
                final var defaultValue = DEFAULT_SIZE;
                final var sanitizedSize = NumberHelper.getDefault(size, defaultValue);
                return NumberHelper.ensureRange(sanitizedSize, 1, 100, defaultValue);
        }

        public static final class Builder {

                private Integer page = DEFAULT_PAGE;
                private Integer size = DEFAULT_SIZE;

                public Builder page(final Integer page) {
                        this.page = page;
                        return this;
                }

                public Builder size(final Integer size) {
                        this.size = size;
                        return this;
                }

                public FindUsersByFilterInputDomain build() {
                        return new FindUsersByFilterInputDomain(this);
                }
        }
}
