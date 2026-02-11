package fittoring.application.exception;

import fittoring.exception.SystemException;

public class DataIntegrityException extends SystemException {

    public DataIntegrityException(String message) {
        super(message);
    }
}
