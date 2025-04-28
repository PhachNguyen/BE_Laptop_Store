package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Cart;
import vn.hoidanit.jobhunter.domain.CartItem;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.CartItemRepository;
import vn.hoidanit.jobhunter.repository.CartRepository;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.repository.userRepository;


import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private userRepository userRepository;

    @Autowired
    private ProductRepo productRepository;

    public List<CartItem> getUserCartItems(String email) {
        User user = userRepository.findByEmail(email);


        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        return cartItemRepository.findByCart(cart);
    }

    public List<CartItem> addToCart(String email, Long productId, int quantity) {
        User user = userRepository.findByEmail(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingItem = cartItemRepository.findByCart(cart).stream()
                .filter(item -> Long.valueOf(item.getProduct().getId()).equals(productId))

                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
//             Cộng dồn sản phẩm
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return cartItemRepository.findByCart(cart);
    }

    public List<CartItem> removeFromCart(String email, Long productId) {
        User user = userRepository.findByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        cartItemRepository.deleteByCartAndProduct(cart, productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found")));

        return cartItemRepository.findByCart(cart);
    }

    public void clearCart(String email) {
        User user = userRepository.findByEmail(email);


        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems());
    }
}
