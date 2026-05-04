package fittoring.application.community.dummy;

public class InvalidDummyScenarioException extends RuntimeException {

    public InvalidDummyScenarioException(String message) {
        super(message);
    }

    public InvalidDummyScenarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
