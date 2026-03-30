package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.SpecialPrice;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SpecialPriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CheckoutService {

    private ProductRepository productRepo;
    private SpecialPriceRepository specialPriceRepo;

    public CheckoutService(ProductRepository productRepo, SpecialPriceRepository specialPriceRepo) {
        this.productRepo = productRepo;
        this.specialPriceRepo = specialPriceRepo;
    }

    /**
     * STRATEGIA
     * Liczy końcową cenę produktów, uwzględniając:
     * - promocje parowe (A/B z C/D)
     * - promocje ilościowe (special price przy zakupie określonej liczby sztuk)
     * - pozostałe produkty normalnie
     */
    public BigDecimal countFinalPrice(List<String> scannedItems) {

        BigDecimal totalCost = BigDecimal.ZERO;


        // Najpierw obsługa promocji parowej
        Set<String> groupAB = Set.of("A", "B");
        Set<String> groupCD = Set.of("C", "D");

        Map<String, Integer> countAB = new HashMap<>();
        Map<String, Integer> countCD = new HashMap<>();
        List<String> leftovers = new ArrayList<>();

        // Liczymy ile mamy produktów w każdej grupie
        for (String item : scannedItems) {
            if (groupAB.contains(item)) {
                countAB.put(item, countAB.getOrDefault(item, 0) + 1);
            } else if (groupCD.contains(item)) {
                countCD.put(item, countCD.getOrDefault(item, 0) + 1);
            } else {
                leftovers.add(item);
            }
        }

        // Ile par da się zrobić?
        int numPairs = Math.min(
                countAB.values().stream().mapToInt(Integer::intValue).sum(),
                countCD.values().stream().mapToInt(Integer::intValue).sum()
        );

        while (numPairs > 0) {

            // wybieramy po jednym produkcie z każdej grupy
            String itemA = null;
            for (Map.Entry<String, Integer> e : countAB.entrySet()) {
                if (e.getValue() > 0) {
                    itemA = e.getKey();
                    break;
                }
            }

            String itemB = null;
            for (Map.Entry<String, Integer> e : countCD.entrySet()) {
                if (e.getValue() > 0) {
                    itemB = e.getKey();
                    break;
                }
            }

            // pobieramy produkty z repo
            String currentItemA = itemA;
            String currentItemB = itemB;

            Product prodA = productRepo.findByItem(currentItemA)
                    .orElseThrow(() -> new NoSuchElementException("Brak produktu " + currentItemA));
            Product prodB = productRepo.findByItem(currentItemB)
                    .orElseThrow(() -> new NoSuchElementException("Brak produktu " + currentItemB));

            // cena specjalna jeśli jest dostępna
            BigDecimal priceA = specialPriceRepo.findByProductItem(itemA)
                    .map(SpecialPrice::getSpecialPrice)
                    .orElse(prodA.getNormalPrice());
            BigDecimal priceB = specialPriceRepo.findByProductItem(itemB)
                    .map(SpecialPrice::getSpecialPrice)
                    .orElse(prodB.getNormalPrice());

            totalCost = totalCost.add(priceA).add(priceB);

            // zmniejszamy liczbę dostępnych produktów
            countAB.put(itemA, countAB.get(itemA) - 1);
            countCD.put(itemB, countCD.get(itemB) - 1);

            numPairs--;
        }
        // Teraz liczymy pozostałe produkty (ilościowe promocje i normalne ceny)
        Map<String, Integer> remainingProducts = new HashMap<>();

        // reszta z grup AB
        for (Map.Entry<String, Integer> e : countAB.entrySet()) {
            if (e.getValue() > 0) remainingProducts.put(e.getKey(), e.getValue());
        }

        // reszta z grup CD
        for (Map.Entry<String, Integer> e : countCD.entrySet()) {
            if (e.getValue() > 0) remainingProducts.put(e.getKey(), e.getValue());
        }

        // produkty spoza par
        for (String item : leftovers) {
            remainingProducts.put(item, remainingProducts.getOrDefault(item, 0) + 1);
        }

        // liczymy cenę z promocją ilościową
        for (Map.Entry<String, Integer> entry : remainingProducts.entrySet()) {

            Product prod = productRepo.findByItem(entry.getKey())
                    .orElseThrow(() -> new NoSuchElementException("Produkt nie znaleziony: " + entry.getKey()));
            int qty = entry.getValue();

            Optional<SpecialPrice> spOpt = specialPriceRepo.findByProductItem(entry.getKey());

            if (spOpt.isPresent()) {
                SpecialPrice sp = spOpt.get();
                int sets = qty / sp.getRequiredQuantity();
                int remainder = qty % sp.getRequiredQuantity();

                BigDecimal setCost = sp.getSpecialPrice().multiply(BigDecimal.valueOf(sets));
                BigDecimal remainingCost = prod.getNormalPrice().multiply(BigDecimal.valueOf(remainder));

                totalCost = totalCost.add(setCost).add(remainingCost);
            } else {
                // brak promocji, normalna cena
                totalCost = totalCost.add(prod.getNormalPrice().multiply(BigDecimal.valueOf(qty)));
            }
        }

        return totalCost;
    }
}