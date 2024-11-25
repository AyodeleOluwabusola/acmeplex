package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.payment.PaymentRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Payment;
import com.uofc.acmeplex.logic.IPaymentService;
import com.uofc.acmeplex.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;

    public IResponse makePayment(PaymentRequest paymentRequest) {

        Payment payment = new Payment();
        payment.setCvv(paymentRequest.getCvv());
        payment.setCardType(paymentRequest.getCardType());
        payment.setCardNumber(paymentRequest.getCardNumber());
        payment.setCardHolderName(paymentRequest.getCardHolderName());
        payment.setExpiryDate(paymentRequest.getExpiryDate());

        // Save the PaymentDetails to the database and return response
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, paymentRepository.save(payment));
    }
}
