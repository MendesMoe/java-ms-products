package com.postech.msproducts.service;

import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductDTO createProduct(ProductDTO productDTO){
        Product newProd = new Product(productDTO);
        newProd.setId(UUID.randomUUID());
        newProd.setCreated_at(LocalDateTime.now());
        return productRepository.save(newProd).toDTO();
    }

    public Product findById(UUID id){
        return productRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("The productId has not found"));
    }

    public ProductDTO updateStockIncrease(UUID id, int quantity){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("The productId has not found"));

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() + quantity);
            return productRepository.save(product).toDTO();
        }
        throw new RuntimeException("Product not found");
    }

    public ProductDTO updateStockDecrease(UUID id, int quantity){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("The productId has not found"));

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() - quantity);
            return productRepository.save(product).toDTO();
        }
        throw new RuntimeException("Product not found");
    }

    public ProductDTO updateStockQuantity(UUID id, int newQuantity){//Product product = productRepository.updateQuantityByCSV(id).orElse(null);
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("The productId has not found"));
        product.setQuantity_stk(newQuantity);
        productRepository.save(product);
        return product.toDTO();
    }

    public Iterable<Product> findAll(){
        List<Product> products = productRepository.findAll();
        //List<ProductDTO> productDTOs = products.stream().map(Product::toDTO).toList();
        return products;
    }

    public void deleteById(UUID id){
        productRepository.deleteById(id);
    }
}

