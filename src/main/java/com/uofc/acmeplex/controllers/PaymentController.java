package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.payment.PaymentRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.IPaymentService;
import com.uofc.acmeplex.security.RequestBean;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("payment")
public class PaymentController {

    private final IPaymentService paymentService;
    private final RequestBean requestBean;

    @PostMapping
    public IResponse makePayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        paymentRequest.setPrincipal(requestBean.getPrincipal());
        return paymentService.makePayment(paymentRequest);
    }

    @GetMapping("cards")
    public IResponse fetchAllUserCards(Pageable pageable) {
        return paymentService.fetchAllUserCards(pageable);
    }
}
