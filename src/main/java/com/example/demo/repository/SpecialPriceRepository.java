package com.example.demo.repository;

import com.example.demo.model.SpecialPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialPriceRepository extends JpaRepository<SpecialPrice, Long> {
    Optional<SpecialPrice> findByProductItem(String item);
}
