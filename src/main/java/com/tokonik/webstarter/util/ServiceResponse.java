package com.tokonik.webstarter.util;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ServiceResponse<T> {

//    public enum StatusCode{
//        OK,
//        ERROR,
//        INVALID_ENTITY,
//        NOT_FOUND,
//        ALREADY_EXISTS
//    }

    private  T body;
    private  int status;
    private  HttpStatus statusCode;
    private  List<String> messages;
    private String path;


    public ServiceResponse(T body, HttpStatus statusCode, List<String> messages) {
        this.statusCode = statusCode;
        this.messages = messages==null? new ArrayList<>(): messages;
        this.body = body;
        this.status = statusCode.value();
    }

    public static <S> ServiceResponse<S> of(S body, HttpStatus statusCode, List<String> messages){
        return  new ServiceResponse<S>(body,statusCode,messages);
    }

    public static ServiceResponse notSupported(){
        return new ServiceResponse<>(null, HttpStatus.METHOD_NOT_ALLOWED, List.of("Method not supported"));

    }

    public ServiceResponse(HttpStatus statusCode, List<String> messages) {
        this(null, statusCode,messages);
    }

    public ServiceResponse() {
        this.messages = new ArrayList<>();
    }


    public T getBody() {
        return body;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public List<String> getMessages() {
        if(messages==null)
            return List.of();
        return messages;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public void setStatusCode(HttpStatus statusCode) {
        if(this.statusCode ==null)
            this.statusCode = statusCode;

        else if(this.statusCode.value()>= statusCode.value())
            this.statusCode = statusCode;

    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message){
        this.messages.add(message);
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
