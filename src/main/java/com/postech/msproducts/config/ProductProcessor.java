package com.postech.msproducts.config;

import com.postech.msproducts.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class ProductProcessor implements ItemProcessor<Product, Product> {
    @Override
    public Product process(Product item) throws Exception {

        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString()); // Gera e define o UUID para novos produtos
            item.setCreated_at(LocalDateTime.now()); // Gera e define o created_at para novos produtos
        }

        return item;
    }
}
