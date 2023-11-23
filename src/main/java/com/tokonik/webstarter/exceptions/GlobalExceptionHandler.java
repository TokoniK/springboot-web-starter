package com.tokonik.webstarter.exceptions;

//import learn.field_agent.controllers.ErrorResponse;
//import jakarta.servlet.ServletException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import com.tokonik.webstarter.util.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Set;

//@EnableWebMvc
@ControllerAdvice()//"ng.gov.cbn.vpmsapi.controllers")
public class GlobalExceptionHandler  {

//    @ExceptionHandler(DataAccessException.class)
//    public ResponseEntity<ErrorResponse> handleException(DataAccessException ex) {
//
//        return new ResponseEntity<ErrorResponse>(
//                new ErrorResponse("We can't show you the details, but something went wrong in our database. Sorry :("),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException ex) {
//
//
//        return new ResponseEntity<ErrorResponse>(
//                new ErrorResponse(ex.getMessage()),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }



    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFoundError(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                "path does not exists",
                HttpStatus.NOT_FOUND);
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler({Exception.class, ServletException.class})
//    public ResponseEntity<String> handleException(Exception ex) {
//
//        //TODO Log the exception
//
//        return new ResponseEntity<>(
//                "Something went wrong on our end. Your request failed",
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler({ResponseException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponses(@ApiResponse(content = @Content(mediaType = "application/json")))
    public ResponseEntity<ServiceResponse> handleResponseException(ResponseException ex) {
        //TODO Log the exception
        return new ResponseEntity<>(
                ex.getResponse(),
                ex.getStatusCode());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
//    @ApiResponses({@ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "405")})
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(
                ex.getFieldError().getDefaultMessage(),
                ex.getStatusCode());
    }

    @ExceptionHandler({ConstraintViolationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Set<ConstraintViolation<?>>> handleMethodArgumentNotValidException(ConstraintViolationException ex) {

        return new ResponseEntity<>(
                ex.getConstraintViolations(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleMethodHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        return new ResponseEntity<>(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<HashMap<String, String>> handleResponseException(AuthenticationException ex) {
        //TODO Log the exception
        HashMap<String, String> response = new HashMap<String, String>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(
                response,
                ex.getStatusCode());
    }


}
