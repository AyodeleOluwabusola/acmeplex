package com.uofc.acmeplex.dto.request.mail;

import com.uofc.acmeplex.entities.Movie;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@ToString
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = -8803978232945381657L;

    private String messageBody;
    private String subject;
    private String messageType;
    private String recipient;
    private String[] recipients;
    private MessageSubTypeEnum messageSubType = MessageSubTypeEnum.WELCOME;
    private String firstName;
    private LocalDateTime showTime;
    private String ticketCode;
    private String seats;
    private String movieName;
    private Map<String, String> details;
    private Movie movie;
    private String theatre;

    //Payment
    private String paymentReference;
    private String currentDate;
    private String totalAmount;
    private String cardType;
    private String cardHolderName;
    private String email;

}
