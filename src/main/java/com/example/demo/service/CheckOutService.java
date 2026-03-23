package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.SpecialPrice;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SpecialPriceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class CheckOutService {
    ProductRepository productRepo;
    SpecialPriceRepository specialPriceRepo;

    public  BigDecimal countFinalPrice(List<String> scannedProducts){
        BigDecimal finalPrice = BigDecimal.ZERO;
        HashMap<String, Integer> counted = new HashMap<>();
        //product count
        for(String scannedProduct : scannedProducts){
            int temp = counted.getOrDefault(scannedProduct,0);
            counted.put(scannedProduct,temp +1);
        }
        for(Map.Entry<String, Integer> quantity : counted.entrySet() )
        {
           Optional<Product>product = productRepo.findByItem(quantity.getKey());
           if(product.isEmpty()){
               throw new NoSuchElementException("Product not found");
           }
           Product tempProduct = product.get();
           Optional<SpecialPrice>specialPrice = specialPriceRepo.findByProductItem(quantity.getKey());
           if(specialPrice.isEmpty()){
               //Normal price
               BigDecimal normalPrice = tempProduct.getNormalPrice();
               finalPrice = finalPrice.add(normalPrice.multiply(BigDecimal.valueOf(quantity.getValue())));
           }
           else{
               BigDecimal normalPrice = tempProduct.getNormalPrice();
               SpecialPrice tempSpecialPrice = specialPrice.get();
               int requiredQuantity = tempSpecialPrice.getRequiredQuantity();
               BigDecimal salePrice = tempSpecialPrice.getSpecialPrice();
               int quanititySale = quantity.getValue()/requiredQuantity;
               int leftItems = quantity.getValue()%requiredQuantity;
               finalPrice = finalPrice.add(salePrice.multiply(BigDecimal.valueOf(quanititySale)).add(normalPrice.multiply(BigDecimal.valueOf(leftItems))));

           }
        }
        return  finalPrice;
    }

}
