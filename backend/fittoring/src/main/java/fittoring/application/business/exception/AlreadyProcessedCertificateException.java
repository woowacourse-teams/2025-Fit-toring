package fittoring.application.business.exception;

public class AlreadyProcessedCertificateException extends RuntimeException {

    public AlreadyProcessedCertificateException(String message) {
        super(message);
    }
}
