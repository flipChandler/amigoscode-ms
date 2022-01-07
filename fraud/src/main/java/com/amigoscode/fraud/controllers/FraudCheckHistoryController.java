package com.amigoscode.fraud.controllers;

import com.amigoscode.fraud.dtos.FraudCheckResponse;
import com.amigoscode.fraud.services.FraudCheckHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fraud-check")
@AllArgsConstructor
public class FraudCheckHistoryController {

    private FraudCheckHistoryService fraudCheckHistoryService;

    @GetMapping(value = "{customerId")
    public FraudCheckResponse isFraudster(@PathVariable Integer customerId) {
        boolean isFraudulentCustomer = fraudCheckHistoryService.isFraudulentCustomer(customerId);
        return new FraudCheckResponse(isFraudulentCustomer);
    }
}
