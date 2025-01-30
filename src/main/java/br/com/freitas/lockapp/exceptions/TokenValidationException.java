package br.com.freitas.lockapp.exceptions;

public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
