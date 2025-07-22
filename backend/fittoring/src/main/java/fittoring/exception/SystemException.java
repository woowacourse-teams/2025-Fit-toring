package fittoring.exception;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }
}
