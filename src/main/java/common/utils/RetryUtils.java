package common.utils;

import common.helpers.StepLogger;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryUtils {
    public static <T> T retry(
            String title,
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts,
            long delayMillis) {

        T result = null;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;
            try {
                result = StepLogger.log("Attempt " + attempts + " of " + maxAttempts + " for " + title, () -> action.get());
                if (condition.test(result)) {
                    return result;
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Retry failed after " + maxAttempts + " attempts");
    }
}
