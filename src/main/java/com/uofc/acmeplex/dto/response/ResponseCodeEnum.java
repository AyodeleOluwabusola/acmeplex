package com.uofc.acmeplex.dto.response;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {

    SUCCESS("00", "Request processed successfully"),
    SERVER_ERROR("99", "Service Unavailable. Kindly contact support"),
    ERROR("99", "Error occurred. Kindly contact support"),
    INVALID_REQUEST("-8", "Invalid request" ),
    BAD_REQUEST("-7", "Bad request Kindly confirm your input" ),
    NO_MESSAGE_PASSED("-98", "Message body is blank"),
    PAYMENT_SUCCESSFUL("00", "Payment Successful"),
    ;

    private final String status;
    private final String message;
    ResponseCodeEnum (String status, String message) {

        this.status = status;
        this.message = message;
    }

}
