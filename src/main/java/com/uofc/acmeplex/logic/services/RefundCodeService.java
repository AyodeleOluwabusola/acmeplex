package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.RefundCode;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.repository.RefundCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class RefundCodeService {

    private final RefundCodeRepository refundCodeRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    public IResponse createRefundCode(Float amount, String email) {

        String uniqueCode;
        do {
            uniqueCode = generateUniqueCode();
        } while (refundCodeRepository.existsByCode(uniqueCode));

        // Create a new RefundCode entity
        RefundCode refundCode = new RefundCode();
        refundCode.setCode(uniqueCode);
        refundCode.setExpiryDate(LocalDate.now().plusYears(1));
        refundCode.setAmount(amount);
        refundCode.setBalance(amount);
        refundCode.setCreatedByEmail(email);

        // Save the RefundCode entity to the database and return response
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, refundCodeRepository.save(refundCode));
    }

    public IResponse applyRefundCode(String code) {
        // Check if the Refund code exists
        RefundCode refundCode = refundCodeRepository.findByCode(code)
                .orElseThrow(() -> new CustomException("Refund code not found", HttpStatus.NOT_FOUND));

        // Check if the Refund code is active and within the valid date range
        LocalDate today = LocalDate.now();
        if (!refundCode.getActive() || today.isAfter(refundCode.getExpiryDate())) {
            throw new CustomException("Refund code is not valid or has expired", HttpStatus.BAD_REQUEST);
        }

        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, refundCodeRepository.save(refundCode));
    }

    public static String generateUniqueCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
