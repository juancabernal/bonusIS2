package co.edu.uco.ucochallenge.domain.user.find.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import co.edu.uco.ucochallenge.crosscutting.helper.ObjectHelper;

@JsonDeserialize(builder = FindUsersByFilterResponseDomain.Builder.class)
public class FindUsersByFilterResponseDomain {

        private final List<UserSummaryDomain> users;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        private FindUsersByFilterResponseDomain(final Builder builder) {
                this.users = List.copyOf(ObjectHelper.getDefault(builder.users, Collections.emptyList()));
                this.page = builder.page;
                this.size = builder.size;
                this.totalElements = builder.totalElements;
                this.totalPages = builder.totalPages;
        }

        public static Builder builder() {
                return new Builder();
        }

        public List<UserSummaryDomain> getUsers() {
                return users;
        }

        public int getPage() {
                return page;
        }

        public int getSize() {
                return size;
        }

        public long getTotalElements() {
                return totalElements;
        }

        public int getTotalPages() {
                return totalPages;
        }

        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {

                private List<UserSummaryDomain> users = Collections.emptyList();
                private int page;
                private int size;
                private long totalElements;
                private int totalPages;

                public Builder users(final List<UserSummaryDomain> users) {
                        this.users = users;
                        return this;
                }

                public Builder page(final int page) {
                        this.page = page;
                        return this;
                }

                public Builder size(final int size) {
                        this.size = size;
                        return this;
                }

                public Builder totalElements(final long totalElements) {
                        this.totalElements = totalElements;
                        return this;
                }

                public Builder totalPages(final int totalPages) {
                        this.totalPages = totalPages;
                        return this;
                }

                public FindUsersByFilterResponseDomain build() {
                        return new FindUsersByFilterResponseDomain(this);
                }
        }
}
