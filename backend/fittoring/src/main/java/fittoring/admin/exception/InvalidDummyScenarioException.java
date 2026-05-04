package fittoring.admin.exception;

public class InvalidDummyScenarioException extends RuntimeException {

    public InvalidDummyScenarioException(String message) {
        super(message);
    }

    public InvalidDummyScenarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
