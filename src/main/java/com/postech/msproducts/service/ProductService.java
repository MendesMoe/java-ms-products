package com.postech.msproducts.service;

import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository  productRepository;

    public Product createProduct(ProductDTO productDTO){
        Product newProd = new Product(productDTO);
        return productRepository.save(newProd);
    }

    public Product findById(UUID id){
        //TODO check if exists
        return productRepository.findById(id).get();
    }

    public Product updateStockIncrease(UUID id, int quantity){
        Product product = productRepository.findById(id).orElse(null);

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() + quantity);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found");
    }

    public Product updateStockDecrease(UUID id, int quantity){
        Product product = productRepository.findById(id).orElse(null);

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() - quantity);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found");
    }

    public Product updateStockQuantity(UUID id, int quantity){//Product product = productRepository.updateQuantityByCSV(id).orElse(null);
        return new Product();
    }

    public Iterable<Product> findAll(){
        return productRepository.findAll();
    }

    public void deleteById(UUID id){
        productRepository.deleteById(id);
    }
}

