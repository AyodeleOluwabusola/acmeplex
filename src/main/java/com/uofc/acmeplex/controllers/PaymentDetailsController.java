package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.payment.PaymentRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("payment")
public class PaymentDetailsController {

    private final IPaymentService paymentService;

    @PostMapping
    public IResponse makePayment(@RequestBody @Valid PaymentRequest theatreInfo) {
        return paymentService.makePayment(theatreInfo);
    }

}
