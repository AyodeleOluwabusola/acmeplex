package com.uofc.acmeplex.dto.request.mail;

import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = -8803978232945381657L;

    private String messageBody;
    private String postMessage;
    private String subject;
    private String messageType;
    private String recipient;
    private String[] recipients;
    private String source;
    private String code;
    private String requestTime;
    private MessageSubTypeEnum messageSubType = MessageSubTypeEnum.WELCOME;
    private String action;
    private String linkUrl;
    private String firstName;
    private String totalAmount;
    private Map<String, String> details;

}
