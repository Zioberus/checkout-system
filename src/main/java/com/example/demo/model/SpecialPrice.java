package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class SpecialPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "productItem", referencedColumnName = "item")
    private Product product;
    private int requiredQuantity;
    private BigDecimal specialPrice;

    public SpecialPrice(){}



}
