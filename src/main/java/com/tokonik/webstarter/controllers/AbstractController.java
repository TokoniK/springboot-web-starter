package com.tokonik.webstarter.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.tokonik.webstarter.exceptions.ResponseException;
import com.tokonik.webstarter.services.AbstractService;
import com.tokonik.webstarter.util.ServiceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractController<E extends Serializable, S extends AbstractService<E>, D> {

    private S service;

    @GetMapping()
    public ResponseEntity<List<E>> getAll(){
        ServiceResponse<List<E>> serviceResponse = service.getAll();

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> getOne(@PathVariable Integer id, HttpServletRequest request) throws ResponseException {
        ServiceResponse<E> serviceResponse = service.getById(id);
        if(!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<E> delete(@PathVariable Integer id, HttpServletRequest request) throws  ResponseException{
        ServiceResponse<E> serviceResponse = service.deleteById(id);
        if (!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());

    }

    @PostMapping()
    public ResponseEntity<E> create(@Valid @RequestBody D object, HttpServletRequest request) throws  ResponseException{

        ServiceResponse<E> serviceResponse = service.create(object);
        if (!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());

    }

    @PutMapping
    public ResponseEntity<E> update(@Valid @RequestBody D object, HttpServletRequest request) throws  ResponseException{

        ServiceResponse<E> serviceResponse = service.update(object);
        if (!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());

    }

    @PutMapping("/{id}")
    public ResponseEntity<E> updateById(@Valid @RequestBody D object, Integer id, HttpServletRequest request) throws  ResponseException{

        ServiceResponse<E> serviceResponse = service.updateById(object, id);
        if (!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());

    }

    @PatchMapping("/{id}")
    public ResponseEntity<E> patch(@RequestBody D object , @PathVariable Integer id, HttpServletRequest request) throws  ResponseException{

        ServiceResponse<E> serviceResponse = service.patchById(object, id);
        if (!serviceResponse.getStatusCode().is2xxSuccessful() && !serviceResponse.getStatusCode().is1xxInformational())
            throw new ResponseException(serviceResponse, request.getRequestURI());

        return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse.getBody());

    }

    public void setService(S service){
        this.service = service;
    }

}
