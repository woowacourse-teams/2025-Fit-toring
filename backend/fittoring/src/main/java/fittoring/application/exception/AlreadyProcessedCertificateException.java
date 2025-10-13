package fittoring.application.exception;

public class AlreadyProcessedCertificateException extends RuntimeException {

    public AlreadyProcessedCertificateException(String message) {
        super(message);
    }
}
