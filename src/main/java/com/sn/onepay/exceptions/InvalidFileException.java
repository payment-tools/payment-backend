package com.sn.onepay.exceptions;

import java.io.Serial;

/*Fichier envoyé illisible ou au mauvais format → 400*/
public class InvalidFileException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidFileException(String message) {
        super(message);
    }
}
