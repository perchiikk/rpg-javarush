package com.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestException extends RuntimeException{

    public RequestException() {
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }
}
