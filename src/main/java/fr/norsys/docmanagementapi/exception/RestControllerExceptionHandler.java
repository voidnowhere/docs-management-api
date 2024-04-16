package fr.norsys.docmanagementapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Void> resourceNotFound(ResourceNotFound ex) {
        return ResponseEntity.notFound().build();
    }
}
