package com.uofc.acmeplex.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uofc.acmeplex.enums.CardTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentRequest {

    private CardTypeEnum cardType;

    @NotBlank(message = "Card number is required")
    @Size(min = 13, max = 19, message = "Card number must be between 13 and 19 characters")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotNull(message = "Amount is required")
    private Float amount;

    @NotBlank(message = "UseCase is required") //Invoice table
    private String useCase;

    @NotBlank(message = "Email address is required")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yyyy")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "Date must be in the format MM/yyyy")
    private String expiryDate;

    @NotBlank(message = "CVV is required")
    private String cvv;

    private Long cardId;

    private boolean saveCard;
}
