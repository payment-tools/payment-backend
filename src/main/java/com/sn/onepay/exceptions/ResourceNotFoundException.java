package com.sn.onepay.exceptions;

import java.io.Serial;
import java.text.MessageFormat;

public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String entity, String param, Object value) {
        super(MessageFormat.format("{0} not found with {1} : {2}", entity, param, value));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
