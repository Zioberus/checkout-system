package com.example.demo.controller;


import com.example.demo.service.CheckoutService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService){
        this.checkoutService = checkoutService;
    }


    @PostMapping("/api/checkout")
    public BigDecimal calculateTotalPrice(@RequestBody List<String> scanned ){

        return checkoutService.countFinalPrice(scanned);


    }
}
