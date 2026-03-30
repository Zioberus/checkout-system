package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.SpecialPrice;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SpecialPriceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckoutServiceTest {

    @Test
    void countFinalPriceNormal(){
        //arrange

        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository,specialPriceRepository);
        Product productA = new Product();
        Product productB = new Product();
        Product productC = new Product();
        Product productD = new Product();
        productA.setNormalPrice(BigDecimal.valueOf(40));
        productA.setItem("A");
        productB.setNormalPrice(BigDecimal.valueOf(10));
        productB.setItem("B");
        productC.setNormalPrice(BigDecimal.valueOf(30));
        productC.setItem("C");
        productD.setNormalPrice(BigDecimal.valueOf(25));
        productD.setItem("D");
        SpecialPrice specialPriceA = new SpecialPrice();
        SpecialPrice specialPriceB = new SpecialPrice();
        SpecialPrice specialPriceC = new SpecialPrice();
        SpecialPrice specialPriceD = new SpecialPrice();
        specialPriceA.setSpecialPrice(BigDecimal.valueOf(30));
        specialPriceA.setProduct(productA);
        specialPriceA.setRequiredQuantity(3);
        specialPriceB.setSpecialPrice(BigDecimal.valueOf(7.5));
        specialPriceB.setProduct(productB);
        specialPriceB.setRequiredQuantity(2);
        specialPriceC.setSpecialPrice(BigDecimal.valueOf(20));
        specialPriceC.setProduct(productC);
        specialPriceC.setRequiredQuantity(4);
        specialPriceD.setSpecialPrice(BigDecimal.valueOf(23.5));
        specialPriceD.setProduct(productD);
        specialPriceD.setRequiredQuantity(2);
        when(productRepository.findByItem("A")).thenReturn(Optional.of(productA));
        when(productRepository.findByItem("B")).thenReturn(Optional.of(productB));
        when(productRepository.findByItem("C")).thenReturn(Optional.of(productC));
        when(productRepository.findByItem("D")).thenReturn(Optional.of(productD));
        when(specialPriceRepository.findByProductItem("A")).thenReturn(Optional.of(specialPriceA));
        when(specialPriceRepository.findByProductItem("B")).thenReturn(Optional.of(specialPriceB));
        when(specialPriceRepository.findByProductItem("C")).thenReturn(Optional.of(specialPriceC));
        when(specialPriceRepository.findByProductItem("D")).thenReturn(Optional.of(specialPriceD));

        //function
        BigDecimal result = checkoutService.countFinalPrice(Arrays.asList("A","A","A","B","B","C","D"));
        assertEquals(0,new BigDecimal("151.0").compareTo(result));
    }
    @Test
    void countFinalPriceOnlyWithPromotion(){
        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository,specialPriceRepository);
        Product productA = new Product();
        Product productC = new Product();
        productA.setNormalPrice(BigDecimal.valueOf(40));
        productA.setItem("A");
        productC.setNormalPrice(BigDecimal.valueOf(30));
        productC.setItem("C");
        SpecialPrice specialPriceA = new SpecialPrice();
        SpecialPrice specialPriceC = new SpecialPrice();
        specialPriceA.setSpecialPrice(BigDecimal.valueOf(30));
        specialPriceA.setProduct(productA);
        specialPriceA.setRequiredQuantity(3);
        specialPriceC.setSpecialPrice(BigDecimal.valueOf(20));
        specialPriceC.setProduct(productC);
        specialPriceC.setRequiredQuantity(4);
        when(productRepository.findByItem("A")).thenReturn(Optional.of(productA));
        when(productRepository.findByItem("C")).thenReturn(Optional.of(productC));
        when(specialPriceRepository.findByProductItem("A")).thenReturn(Optional.of(specialPriceA));
        when(specialPriceRepository.findByProductItem("C")).thenReturn(Optional.of(specialPriceC));
        //function
        BigDecimal result = checkoutService.countFinalPrice(Arrays.asList("A","C"));
        assertEquals(0,new BigDecimal("50").compareTo(result));

    }
    @Test
    void countFinalPriceWithoutPromotion() {
        //arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository,specialPriceRepository);
        Product product = new Product();
        product.setNormalPrice(BigDecimal.valueOf(40));
        product.setItem("A");
        when(productRepository.findByItem("A")).thenReturn(Optional.of(product));
        when(specialPriceRepository.findByProductItem("A")).thenReturn(Optional.empty());
        //function
        BigDecimal result = checkoutService.countFinalPrice(Arrays.asList("A","A"));
        assertEquals(0,new BigDecimal("80").compareTo(result));
    }
    @Test
    void countFinalPriceWithRemainingItems() {
        //arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository, specialPriceRepository);

        Product productA = new Product();
        productA.setNormalPrice(BigDecimal.valueOf(40));
        productA.setItem("A");

        SpecialPrice specialPriceA = new SpecialPrice();
        specialPriceA.setSpecialPrice(BigDecimal.valueOf(30));
        specialPriceA.setProduct(productA);
        specialPriceA.setRequiredQuantity(3);

        when(productRepository.findByItem("A")).thenReturn(Optional.of(productA));
        when(specialPriceRepository.findByProductItem("A")).thenReturn(Optional.of(specialPriceA));
        //function
        BigDecimal result = checkoutService.countFinalPrice(Arrays.asList("A", "A", "A", "A"));
        assertEquals(0, new BigDecimal("70").compareTo(result));
    }
    @Test
    void countFinalPricePairPromotionWithRemainingItems() {
        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository, specialPriceRepository);

        Product productA = new Product();
        Product productC = new Product();
        productA.setNormalPrice(BigDecimal.valueOf(40));
        productA.setItem("A");
        productC.setNormalPrice(BigDecimal.valueOf(30));
        productC.setItem("C");

        SpecialPrice specialPriceA = new SpecialPrice();
        SpecialPrice specialPriceC = new SpecialPrice();
        specialPriceA.setSpecialPrice(BigDecimal.valueOf(30));
        specialPriceA.setProduct(productA);
        specialPriceA.setRequiredQuantity(3);
        specialPriceC.setSpecialPrice(BigDecimal.valueOf(20));
        specialPriceC.setProduct(productC);
        specialPriceC.setRequiredQuantity(4);

        when(productRepository.findByItem("A")).thenReturn(Optional.of(productA));
        when(productRepository.findByItem("C")).thenReturn(Optional.of(productC));
        when(specialPriceRepository.findByProductItem("A")).thenReturn(Optional.of(specialPriceA));
        when(specialPriceRepository.findByProductItem("C")).thenReturn(Optional.of(specialPriceC));

        //function
        BigDecimal result = checkoutService.countFinalPrice(Arrays.asList("A","A","C"));
        assertEquals(0, new BigDecimal("90").compareTo(result));
    }
    @Test
    void throwExceptionNoItemFound()
    {   //arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        SpecialPriceRepository specialPriceRepository = mock(SpecialPriceRepository.class);
        CheckoutService checkoutService = new CheckoutService(productRepository,specialPriceRepository);
        when(productRepository.findByItem("A")).thenReturn(Optional.empty());
        //function and assertThrow
        assertThrows(NoSuchElementException.class, () -> { checkoutService.countFinalPrice(Arrays.asList("A"));});


    }}