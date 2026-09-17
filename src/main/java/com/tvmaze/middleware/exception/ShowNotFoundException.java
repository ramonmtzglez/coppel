package com.tvmaze.middleware.exception;

public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(Long showId) {
        super("Show con id " + showId + " no encontrado");
    }
}
