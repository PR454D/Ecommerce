package com.gaurav.productservice.exception;

import com.gaurav.productservice.model.ErrorResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(ProductServiceException exception){
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(exception.getMessage()).errorCode(exception.getErrorCode()).build(), HttpStatus.NOT_FOUND);

    }
}
