package org.inmobiliaria.apiinmobiliaria.exception;

public class InmuebleNotFoundException extends RuntimeException {
    public InmuebleNotFoundException(String message) {
        super(message);
    }
}
