package com.fintech.platform.merchant.controller;

import com.fintech.platform.merchant.dto.CreateMerchantRequest;
import com.fintech.platform.merchant.dto.MerchantResponse;
import com.fintech.platform.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public MerchantResponse createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return merchantService.createMerchant(request);
    }

    @GetMapping("/{id}")
    public MerchantResponse getMerchantById(@PathVariable UUID id) {
        return merchantService.getMerchantById(id);
    }

    @GetMapping
    public List<MerchantResponse> getAllMerchants() {
        return merchantService.getAllMerchants();
    }
}