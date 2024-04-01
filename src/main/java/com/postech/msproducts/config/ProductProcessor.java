package com.postech.msproducts.config;

import com.postech.msproducts.domain.Product;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductProcessor implements ItemProcessor<Product, Product> {
    @Override
    public Product process(Product item) throws Exception {
        item.setCreated_at(LocalDateTime.now());
        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString()); // Gera e define o UUID
        }
        return item;
    }
}
