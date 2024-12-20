package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.payment.PaymentRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import org.springframework.data.domain.Pageable;

public interface IPaymentService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse makePayment(PaymentRequest paymentRequest);
    IResponse fetchAllUserCards(Pageable pageable);
}
