package vn.hoidanit.jobhunter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import vn.hoidanit.jobhunter.domain.Cart;
import vn.hoidanit.jobhunter.domain.CartItem;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.CartRepository;
import vn.hoidanit.jobhunter.repository.CartItemRepository;
import vn.hoidanit.jobhunter.repository.userRepository;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.service.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private userRepository userRepository;

    @Mock
    private ProductRepo productRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(cartRepository, cartItemRepository, userRepository, productRepository);
    }

    @Test
    void testGetUserCartItems() {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        Cart mockCart = new Cart();
        mockCart.setUser(mockUser);

        CartItem item = new CartItem();
        item.setCart(mockCart);
        item.setQuantity(1);

        when(userRepository.findByEmail("user@example.com")).thenReturn(mockUser);
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartItemRepository.findByCart(mockCart)).thenReturn(List.of(item));

        List<CartItem> result = cartService.getUserCartItems("user@example.com");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getQuantity());
    }

    @Test
    void testAddToCart_NewItem() {
        User user = new User();
        Product product = new Product();
        product.setId(1L);
        Cart cart = new Cart();
        cart.setUser(user);

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(List.of());

        List<CartItem> result = cartService.addToCart("user@example.com", 1L, 2);

        verify(cartItemRepository).save(any(CartItem.class));
        assertNotNull(result);
    }

    @Test
    void testAddToCart_ExistingItem() {
        User user = new User();
        Product product = new Product();
        product.setId(1L);
        Cart cart = new Cart();
        cart.setUser(user);

        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        existingItem.setCart(cart);

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(List.of(existingItem));

        List<CartItem> result = cartService.addToCart("user@example.com", 1L, 2);

        assertEquals(3, existingItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
        assertNotNull(result);
    }

//    @Test
//    void testAddToCart_InvalidProduct() {
//        User user = new User();
//        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
//        when(productRepository.findById(99L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            cartService.addToCart("user@example.com", 99L, 1);
//        });
//
//        assertTrue(exception.getMessage().contains("Không tìm thấy sản phẩm"));
//    }
}
