package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

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

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        // Thêm base URL
        String baseUrl = "http://localhost:8080/storage/";

        // Chuyển đổi image path cho từng sản phẩm trong giỏ
        cartItems.forEach(item -> {
            Product product = item.getProduct();
            if (product != null && product.getImages() != null) {
                List<String> fullImageUrls = product.getImages().stream()
                        .map(fileName -> baseUrl + "product-" + product.getId() + "/" + fileName)
                        .collect(Collectors.toList());
                product.setImages(fullImageUrls);
            }
        });

        return cartItems;
    }


    @Transactional
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
                .filter(item -> item.getProduct().getId() == productId)

                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        System.out.println("Số lượng item trong cart: " + cart.getItems().size());
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public List<CartItem> removeFromCart(String email, Long productId) {
        User user = userRepository.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCartAndProduct(
                cart,
                productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"))
        );

        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems());
    }

    @Transactional
    public List<CartItem> updateQuantity(String email, Long productId, int quantity) {
        User user = userRepository.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng này"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCart(cart).stream()
                .filter(ci -> ci.getProduct().getId() == productId)

                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        System.out.println("Số lượng item trong cart: " + cart.getItems().size());
        return cartItemRepository.findByCart(cart);
    }
}
