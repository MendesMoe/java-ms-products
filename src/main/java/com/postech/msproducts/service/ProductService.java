package com.postech.msproducts.service;

import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.exceptions.NotFoundException;
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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO createProduct(ProductDTO productDTO){
        Product newProd = new Product(productDTO);
        newProd.setId(UUID.randomUUID().toString());
        newProd.setCreated_at(LocalDateTime.now());
        return productRepository.save(newProd).toDTO();
    }

    public Product findById(String id){
        UUID uuid = UUID.fromString(id);
        return productRepository.findById(uuid)
                .orElseThrow(()-> new NotFoundException("The productId has not found"));
    }

    public ProductDTO updateStockIncrease(String id, int quantity){
        UUID uuid = UUID.fromString(id);
        Product product = productRepository.findById(uuid)
                .orElseThrow(()-> new NotFoundException("The productId has not found"));

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() + quantity);
            return productRepository.save(product).toDTO();
        }
        throw new NotFoundException("Product not found");
    }

    public ProductDTO updateStockDecrease(String id, int quantity){
        UUID uuid = UUID.fromString(id);
        Product product = productRepository.findById(uuid)
                .orElseThrow(()-> new NotFoundException("The productId has not found"));

        if (product != null){
            product.setQuantity_stk(product.getQuantity_stk() - quantity);
            return productRepository.save(product).toDTO();
        }
        throw new NotFoundException("Product not found");
    }

    public Iterable<Product> findAll(){
        List<Product> products = productRepository.findAll();
        return products;
    }

    public void deleteById(String id){
        UUID uuid = UUID.fromString(id);
        productRepository.deleteById(uuid);
    }

    public Boolean isProductAvailableById(String id, int qttyNewOrder) {
        UUID uuid = UUID.fromString(id);
        Product product = productRepository.findById(uuid)
                .orElseThrow(()-> new NotFoundException("The productId has not found"));

        if (product != null){
            return product.getQuantity_stk() >= qttyNewOrder;
        }
        throw new NotFoundException("Product not found");
    }
}

