package co.edu.uco.ucochallenge.application.user.search.interactor.dto;

import java.util.List;

public class FindUsersByFilterOutputDTO {

        private List<UserSummaryDTO> users;
        private int page;
        private int size;
        private long totalElements;

        public List<UserSummaryDTO> getUsers() {
                return users;
        }

        public void setUsers(final List<UserSummaryDTO> users) {
                this.users = users;
        }

        public int getPage() {
                return page;
        }

        public void setPage(final int page) {
                this.page = page;
        }

        public int getSize() {
                return size;
        }

        public void setSize(final int size) {
                this.size = size;
        }

        public long getTotalElements() {
                return totalElements;
        }

        public void setTotalElements(final long totalElements) {
                this.totalElements = totalElements;
        }
}
