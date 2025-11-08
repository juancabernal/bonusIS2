package co.edu.uco.ucochallenge.application.common.dto;

import co.edu.uco.ucochallenge.crosscutting.helper.ObjectHelper;

public abstract class Response<T> {

        private boolean dataReturned;
        private T data;

        protected Response(final boolean returnData, final T data) {
                setDataReturned(returnData);
                setData(data);
        }

        private void setDataReturned(final boolean dataReturned) {
                this.dataReturned = dataReturned;
                if (!dataReturned) {
                        this.data = null;
                }
        }

        private void setData(final T data) {
                if (!isDataReturned()) {
                        this.data = null;
                        return;
                }
                this.data = ObjectHelper.getDefault(data, null);
        }

        public boolean isDataReturned() {
                return dataReturned;
        }

        public T getData() {
                return data;
        }

}
