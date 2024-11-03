package com.sn.onepay.exceptions;

import java.io.Serial;

public class ResourceAlreadyExistException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistException(String message) {
        super(message);
    }

    public ResourceAlreadyExistException(String entity, Object value) {
        super(entity + ": already exist for version with reference : " + value);
    }
}
