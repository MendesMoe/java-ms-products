package com.postech.msproducts.service;

import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    AutoCloseable openMocks;

    @BeforeEach
    void setup(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void create_a_product(){
        //arrange
        Product product = newProduct();
        //act
        when(productRepository.save(product)).thenReturn(product);
        when(productService.createProduct(product.toDTO())).thenReturn(product.toDTO());
        //assert
        ProductDTO productCreated = productService.createProduct(product.toDTO());
        //assert
        assertThat(product.getName()).isEqualTo(productCreated.name());
    }

    @Test
    void find_product_by_id(){
        //arrange
        Product product = newProduct();
        //act
        when(productService.findById(product.getId().toString())).thenReturn(product);
        //assert
        Product productFound = productService.findById(product.getId().toString());
        //assert
        assertThat(product.getName()).isEqualTo(productFound.getName());
    }

    @Test
    void update_stock_increase(){
        //arrange
        Product product = newProduct();
        //act
        when(productService.updateStockIncrease(product.getId().toString(), 10)).thenReturn(product.toDTO());
        //assert
        ProductDTO productUpdated = productService.updateStockIncrease(product.getId().toString(), 10);
        assert product.getQuantity_stk() == productUpdated.quantity_stk();
    }

    @Test
    void update_stock_decrease(){
        //arrange
        Product product = newProduct();
        //act
        when(productService.updateStockDecrease(product.getId().toString(), 10)).thenReturn(product.toDTO());
        //assert
        ProductDTO productUpdated = productService.updateStockDecrease(product.getId().toString(), 10);
        assert product.getQuantity_stk() == productUpdated.quantity_stk();
    }

    @Test
    void delete_product_by_id(){
        //arrange
        Product product = newProduct();
        UUID id = UUID.fromString(product.getId());
        //act
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(id);
        //assert
        productService.deleteById("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc");
        //assert
        // ? verify(productRepository, times(1)).deleteById(any(UUID.class));
    }

    public static Product newProduct(){
        ProductDTO productDTO = new ProductDTO("p1", "product-teste", 10.00, 100, null, null);
        UUID id = UUID.fromString("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc");
        Product product = new Product(productDTO);
        product.setId(id.toString());
        return product;
    }
}
