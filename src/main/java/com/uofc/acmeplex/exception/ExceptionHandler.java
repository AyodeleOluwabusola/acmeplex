package com.uofc.acmeplex.exception;

import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handler class for handling different kinds of exceptions
 */
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class ExceptionHandler {


    private final Environment environment;

    boolean isDev = false;

    @PostConstruct
    public void init() {
        isDev = environment.getActiveProfiles().length > 0 && "dev".equalsIgnoreCase(environment.getActiveProfiles()[0]);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity handle(Exception ex) {

        if (ex instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex);
        }
        if (ex instanceof ValidationException) {
            return handleValidationException((ValidationException) ex);
        }

        if (ex instanceof CustomException) {
            return handleValidationException((CustomException) ex);
        }
        log.error("Error occurred", ex);

        var resp = ResponseData.getInstance(ResponseCodeEnum.SERVER_ERROR);
        resp.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String responseDescription = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        messages -> messages.size() == 1 ? messages.get(0) : String.join(", ", messages)
                ));
        ResponseData<Object> response = ResponseData.getInstance(ResponseCodeEnum.INVALID_REQUEST, responseDescription);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    public ResponseEntity handleValidationException(ValidationException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.getInstance(ResponseCodeEnum.BAD_REQUEST, exception.getMessage()));
    }

    public ResponseEntity handleValidationException(CustomException exception) {

        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getResponseObj());
    }

}
