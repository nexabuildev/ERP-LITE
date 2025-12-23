package com.rubensimon1.erp_lite.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Interceptor global
public class GlobalExceptionHandler {
    /*
     * Este es el método que intercepta el error "MethodArgumentNotValidException"
     * que lanza Spring cuando falla el @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        /*
         * Creamos el mapa de errores
         */
        Map<String, String> errores = new HashMap<>();

        /*
         * Extraemos los memnsajes de error amigables
         */
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        /*
         * Devolvemos un JSON limpio con estado 400 (Bad request)
         */
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}
