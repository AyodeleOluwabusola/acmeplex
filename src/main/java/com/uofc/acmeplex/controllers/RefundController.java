package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.IRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("refund")
public class RefundController {

    private final IRefundService refundService;

    @GetMapping("validate")
    public IResponse validateRefundCode(@RequestParam String code) {
        return refundService.validateRefundCode(code);
    }
}
