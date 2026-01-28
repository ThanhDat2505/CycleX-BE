package com.example.cyclexbe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class Handler {

    @ExceptionHandler
  public ResponseEntity<ErrorRespone> checkNotFound(BadRequestException ex){
        ErrorRespone error=new ErrorRespone(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
