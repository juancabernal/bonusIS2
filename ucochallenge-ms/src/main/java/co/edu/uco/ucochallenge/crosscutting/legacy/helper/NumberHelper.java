package co.edu.uco.ucochallenge.crosscutting.legacy.helper;

public final class NumberHelper {

        private NumberHelper() {
        }

        public static int getDefault(final Integer value) {
                return ObjectHelper.getDefault(value, 0);
        }

        public static int getDefault(final Integer value, final int defaultValue) {
                return ObjectHelper.getDefault(value, defaultValue);
        }

        public static int ensureMinimum(final int value, final int minimum, final int defaultValue) {
                if (value < minimum) {
                        return defaultValue;
                }
                return value;
        }

        public static int ensureRange(final int value, final int minimum, final int maximum, final int defaultValue) {
                var sanitized = ensureMinimum(value, minimum, defaultValue);
                if (sanitized > maximum) {
                        return maximum;
                }
                return sanitized;
        }
}
