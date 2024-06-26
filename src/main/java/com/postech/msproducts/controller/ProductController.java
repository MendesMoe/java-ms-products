package com.postech.msproducts.controller;

import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product with a DTO", responses = {
            @ApiResponse(description = "The new product was created", responseCode = "201")
    })
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productdto) {
        ProductDTO productCreated = productService.createProduct(productdto);
        return ResponseEntity.created(null).body(productCreated);
    }

    @GetMapping
    @Operation(summary = "Get all products", responses = {
            @ApiResponse(description = "List of all products", responseCode = "200")
    })
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one product by ID", responses = {
            @ApiResponse(description = "The product with this id", responseCode = "200")
    })
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product.toDTO());
    }

    @GetMapping("/{id}/{qtty}")
    @Operation(summary = "Get the availability of a product by id and the quantity wanted", responses = {
            @ApiResponse(description = "True if the qtty is found", responseCode = "200")
    })
    public Boolean isProductAvailableById(@PathVariable String id, @PathVariable int qtty) {
        return productService.isProductAvailableById(id, qtty);
    }
    @PutMapping("/updateStockIncrease/{id}/{quantity}")
    @Operation(summary = "Increase the stock for one product by ID", responses = {
            @ApiResponse(description = "The stock was updated", responseCode = "200")
    })
    public ResponseEntity<?> updateStockIncrease(@Valid @PathVariable String id, @PathVariable int quantity){
        ProductDTO productDTO = productService.updateStockIncrease(id, quantity);
        return ResponseEntity.ok(productDTO);
    }

    @PutMapping("/updateStockDecrease/{id}/{quantity}")
    @Operation(summary = "Decrease the stock for one product by ID", responses = {
            @ApiResponse(description = "The stock was updated", responseCode = "200")
    })
    public ResponseEntity<?> updateStockDecrease(@Valid @PathVariable String id, @PathVariable int quantity){
        ProductDTO productDTO = productService.updateStockDecrease(id, quantity);
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID", responses = {
            @ApiResponse(description = "The product was deleted", responseCode = "204")
    })
    public ResponseEntity<?> deleteById(@PathVariable String id){
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
