package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.AcmePlexUser;
import com.uofc.acmeplex.entities.PromotionCode;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.repository.PromotionCodeRepository;
import com.uofc.acmeplex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class PromotionCodeService {

    private final PromotionCodeRepository promotionCodeRepository;
    private final UserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    public IResponse createPromotionCode(Float amount, String email) {

        AcmePlexUser user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        String uniqueCode;
        do {
            uniqueCode = generateUniqueCode();
        } while (promotionCodeRepository.existsByCode(uniqueCode));

        // Create a new PromotionCode entity
        PromotionCode promotionCode = new PromotionCode();
        promotionCode.setCode(uniqueCode);
        promotionCode.setExpiryDate(LocalDate.now().plusYears(1));
        promotionCode.setAmount(amount);
        promotionCode.setBalance(amount);
        promotionCode.setCreatedBy(user);

        // Save the PromotionCode entity to the database and return response
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, promotionCodeRepository.save(promotionCode));
    }

    public IResponse applyPromotionCode(String code) {
        // Check if the promotion code exists
        PromotionCode promotionCode = promotionCodeRepository.findByCode(code)
                .orElseThrow(() -> new CustomException("Promotion code not found", HttpStatus.NOT_FOUND));

        // Check if the promotion code is active and within the valid date range
        LocalDate today = LocalDate.now();
        if (!promotionCode.getActive() || today.isAfter(promotionCode.getExpiryDate())) {
            throw new CustomException("Promotion code is not valid or has expired", HttpStatus.BAD_REQUEST);
        }

        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, promotionCodeRepository.save(promotionCode));
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
