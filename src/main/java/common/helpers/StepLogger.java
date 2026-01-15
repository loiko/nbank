package common.helpers;

/*
 Example of usage:

 StepLogger.log("Get all users", () -> {

  click() ->  click log
  post() -> post log

  }

  "Get all users" ->
        "click log"
        "post log"

 */
public class StepLogger {
    @FunctionalInterface
    public interface ThrowableRunnable<T> {
        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowableVoidRunnable {
        void run() throws Throwable;
    }

    public static <T> T log(String title, ThrowableRunnable<T> runnable) {
        System.out.println("[STEP] " + title);
        try {
            T result = runnable.run();
            System.out.println("[STEP COMPLETED] " + title);
            return result;
        } catch (Throwable e) {
            System.err.println("[STEP FAILED] " + title + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void log(String title, ThrowableVoidRunnable runnable) {
        System.out.println("[STEP] " + title);
        try {
            runnable.run();
            System.out.println("[STEP COMPLETED] " + title);
        } catch (Throwable e) {
            System.err.println("[STEP FAILED] " + title + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
