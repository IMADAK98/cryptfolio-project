package com.cryptoflio.portfolio_service.Exception;

import com.cryptoflio.portfolio_service.Entites.ErrorDetails;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .errors(errorList)
                .build();
        return handleExceptionInternal(ex, errorDetails, headers, status, request);
    }




    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = "Invalid value for one of the fields. Please check the request payload.";
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException cause =
                    (com.fasterxml.jackson.databind.exc.InvalidFormatException) ex.getCause();
            if (cause.getTargetType().isEnum()) {
                errorMessage = String.format("Invalid value '%s' for field '%s'. Allowed values are %s",
                        cause.getValue(), cause.getPath().get(0).getFieldName(), Arrays.toString(cause.getTargetType().getEnumConstants()));
            }
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(errorMessage)
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AssetCreationException.class)
    protected final ResponseEntity<Object> handleCreationException(Exception ex, WebRequest req) {
        ErrorDetails details = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(req.getDescription(false))
                .build();

        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected final ResponseEntity<Object> handleMissingRequestHeader(Exception ex, WebRequest req) {
        ErrorDetails details = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(req.getDescription(false))
                .build();

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }


}
