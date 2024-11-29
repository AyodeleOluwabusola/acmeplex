package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.mail.EmailMessage;
import com.uofc.acmeplex.dto.request.payment.PaymentRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.AcmePlexUser;
import com.uofc.acmeplex.entities.Card;
import com.uofc.acmeplex.entities.Invoice;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.IPaymentService;
import com.uofc.acmeplex.mail.EmailService;
import com.uofc.acmeplex.repository.CardRepository;
import com.uofc.acmeplex.repository.InvoiceRepository;
import com.uofc.acmeplex.repository.UserRepository;
import com.uofc.acmeplex.security.RequestBean;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class PaymentService implements IPaymentService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final RequestBean requestBean;
    private final EmailService emailService;

    public IResponse makePayment(PaymentRequest paymentRequest) {

        Card card = new Card();
        card.setCvv(paymentRequest.getCvv());
        card.setCardType(paymentRequest.getCardType());
        card.setCardNumber(paymentRequest.getCardNumber());
        card.setCardHolderName(paymentRequest.getCardHolderName());
        card.setExpiryDate(paymentRequest.getExpiryDate());

        if (StringUtils.isNotBlank(requestBean.getPrincipal())){{
            AcmePlexUser user = userRepository.findByEmail(requestBean.getPrincipal())
                    .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
            card.setUser(user);
        }}
        cardRepository.save(card);

        Invoice invoice = new Invoice();
        invoice.setPaymentReference("PYR" + card.getId() + System.currentTimeMillis());
        invoice.setAmount(paymentRequest.getAmount());
        invoice.setCard(card);
        invoiceRepository.save(invoice);

        sendPaymentNotification(card, invoice);
        return ResponseData.getInstance(ResponseCodeEnum.PAYMENT_SUCCESSFUL, invoice);
    }

    private void sendPaymentNotification(Card card, Invoice invoice) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFirstName(card.getUser().getFirstName());
        emailMessage.setRecipient(card.getUser().getEmail());
        emailMessage.setMessageType("EMAIL");
        emailMessage.setMessageBody("Receipt notification");
        emailMessage.setSubject("Receipt notification");
        var subType = MessageSubTypeEnum.PAYMENT_CONFIRMATION;

        emailMessage.setMessageSubType(subType);
        emailMessage.setPaymentReference(invoice.getPaymentReference());
        emailMessage.setTotalAmount("$ " + invoice.getAmount());
        emailMessage.setCardType(card.getCardType().getType());
        emailMessage.setCardHolderName(card.getCardHolderName());
        emailMessage.setBillingAddress(card.getCardHolderName());

        CompletableFuture.runAsync(()-> emailService.sendSimpleMail(emailMessage));
    }

    @Override
    public IResponse fetchAllUserCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findAllByUserEmail(requestBean.getPrincipal(), pageable);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, cards.getContent());
    }
}
