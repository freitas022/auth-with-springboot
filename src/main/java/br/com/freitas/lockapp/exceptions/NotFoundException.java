package br.com.freitas.lockapp.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Resource not found!");
    }
}
