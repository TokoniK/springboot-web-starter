package com.tokonik.webstarter.exceptions;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends ServletException {

    @Override
    public String getMessage() {
        String message = "Authentication failed";
        return message;
    }

    public HttpStatus getStatusCode(){
        return HttpStatus.UNAUTHORIZED;
    }
}
