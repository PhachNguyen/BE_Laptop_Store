package vn.hoidanit.jobhunter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepo);
    }

    @Test
    void testCreateProduct() {
        Product newProduct = new Product();
        newProduct.setName("Laptop A");

        when(productRepo.save(newProduct)).thenReturn(newProduct);

        Product result = productService.createProduct(newProduct);

        assertNotNull(result);
        assertEquals("Laptop A", result.getName());
    }

    @Test
    void testGetProductById_Found() {
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("iPhone");

        when(productRepo.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("iPhone", result.getName());
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product(); p1.setName("Phone");
        Product p2 = new Product(); p2.setName("Tablet");

        when(productRepo.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Phone", result.get(0).getName());
    }

    @Test
    void testDeleteProductById() {
        long productId = 10L;

        doNothing().when(productRepo).deleteById(productId);

        productService.deleteProduct(productId);

        verify(productRepo, times(1)).deleteById(productId);
    }
}
