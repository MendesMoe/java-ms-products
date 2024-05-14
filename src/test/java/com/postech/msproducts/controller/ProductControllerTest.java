package com.postech.msproducts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.msproducts.domain.Product;
import com.postech.msproducts.domain.ProductDTO;
import com.postech.msproducts.exceptions.GlobalExceptionHandler;
import com.postech.msproducts.exceptions.NotFoundException;
import com.postech.msproducts.repository.ProductRepository;
import com.postech.msproducts.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;  // Mock se necessário

    private AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldCreateAProduct() throws Exception {
        Product prod = newProduct();
        ProductDTO productDTO = prod.toDTO();
        when(productService.createProduct(productDTO)).thenReturn(productDTO);

        String productCreateDTOJson = new ObjectMapper().writeValueAsString(productDTO);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productCreateDTOJson))
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(productDTO);
    }

    @Test
    void shouldFindProductById() throws Exception {
        Product prod = newProduct();
        when(productService.findById("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc")).thenReturn(prod);

        String expectedJson = new ObjectMapper().writeValueAsString(prod.toDTO());

        mockMvc.perform(get("/api/products/{id}", "cb8f986a-0bd5-485c-8a4e-3f369ce13ecc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(productService, times(1)).findById("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc");
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product prod = newProduct();
        List<Product> products = List.of(prod);
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).findAll();
    }

    @Test
    void shouldCheckIfAProductIsAvailableById() throws Exception {
        Product prod = newProduct();
        when(productService.findById("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc")).thenReturn(prod);

        mockMvc.perform(get("/api/products/{id}/{qtty}", "cb8f986a-0bd5-485c-8a4e-3f369ce13ecc", 10)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).isProductAvailableById("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc", 10);
    }

    @Test
    public void updateStockIncrease_ShouldIncreaseProductStock() throws Exception {
        String productId = UUID.randomUUID().toString();
        int increaseQuantity = 10;
        ProductDTO mockProductDTO = new ProductDTO(
                "Product Name",
                "Product Description",
                20.00,
                100 + increaseQuantity,  // Assume original stock was 100 for testing
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(this.productService.updateStockIncrease(productId, increaseQuantity)).thenReturn(mockProductDTO);

        mockMvc.perform(put("/api/products/updateStockIncrease/{id}/{quantity}", productId, increaseQuantity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity_stk").value(110));  // Expected quantity after increase
                //.andDo(print());

        verify(this.productService).updateStockIncrease(productId, increaseQuantity);
    }

    @Test
    public void updateStockDecrease_ShouldDecreaseProductStock() throws Exception {
        String productId = UUID.randomUUID().toString();
        int decreaseQuantity = 10;
        ProductDTO mockProductDTO = new ProductDTO(
                "Product Name",
                "Product Description",
                20.00,
                100 - decreaseQuantity,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(this.productService.updateStockDecrease(productId, decreaseQuantity)).thenReturn(mockProductDTO);

        mockMvc.perform(put("/api/products/updateStockDecrease/{id}/{quantity}", productId, decreaseQuantity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity_stk").value(90));
                //.andDo(print());

        verify(this.productService).updateStockDecrease(productId, decreaseQuantity);
    }

    /*
    @Test
void shouldReturnNotFoundWhenProductNotFound() throws Exception {
    UUID productId = UUID.randomUUID();
    when(productService.getProductById(productId)).thenThrow(new ProductNotFoundException("Not found"));

    mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProductNotFoundException))
            .andExpect(jsonPath("$.message").value("Not found"));
}
     */
    //TODO criar as exceptions

    @Test
    public void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ProductDTO productdto = new ProductDTO("", "", null, 0, null, null);  // Dados inválidos
        String jsonContent = new ObjectMapper().writeValueAsString(productdto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteById_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        String nonExistentId = "nonexistent-id";
        doThrow(new NotFoundException("Product not found")).when(productService).deleteById(nonExistentId);

        mockMvc.perform(delete("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    public static Product newProduct() {
        ProductDTO productDTO = new ProductDTO("p1", "product-teste", 10.00, 100, null, null);
        UUID id = UUID.fromString("cb8f986a-0bd5-485c-8a4e-3f369ce13ecc");
        Product product = new Product(productDTO);
        product.setId(id.toString());
        return product;
    }
}
