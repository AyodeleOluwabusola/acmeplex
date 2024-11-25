package com.uofc.acmeplex.exception;

import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseData;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling API exceptions
 */
@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public CustomException(String message, HttpStatus httpStatus) {
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public IResponse getResponseObj() {
        var resp = new ResponseData<>();
        resp.setStatus("99");
        resp.setMessage(message);
        return resp;
    }
}
