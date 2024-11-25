package com.uofc.acmeplex.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uofc.acmeplex.enums.CardTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentRequest {

    private CardTypeEnum cardType;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    private Float amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yyyy")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", message = "Date must be in the format MM/yyyy")
    private String expiryDate;

    @NotBlank(message = "CVV is required")
    private String cvv;
}
