package com.gradlic.fts.erp.exception;

import com.gradlic.fts.erp.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(fieldMessage)
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    protected ResponseEntity<Object> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage().contains("Duplicate Entry") ? "Information already present": exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> badCredentialsException(BadCredentialsException exception) {
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage() + ", Incorrect email or password.")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> apiException(ApiException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> accessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason(exception.getMessage() != null ? exception.getMessage().contains("expected 1, actual 0") ? "Record not found" : exception.getMessage() : "Some Error Occurred")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<Object> disabledException(DisabledException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason("User account is currently disabled")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<Object> lockedException(LockedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason("User account is currently locked")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> exception(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder().timeStamp(now().toString())
                        .reason("Something went wrong")
                        .developerMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

}
