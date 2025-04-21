package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.CartItem;
import vn.hoidanit.jobhunter.service.CartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Lấy giỏ hàng
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        List<CartItem> items = cartService.getUserCartItems(email);
        return ResponseEntity.ok(items);
    }

    // Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<List<CartItem>> addToCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Long> body
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        Long productId = body.get("productId");
        List<CartItem> updatedCart = cartService.addToCart(email, productId);
        return ResponseEntity.ok(updatedCart);
    }

    // Xoá sản phẩm khỏi giỏ
    @DeleteMapping("/{productId}")
    public ResponseEntity<List<CartItem>> removeFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        List<CartItem> updatedCart = cartService.removeFromCart(email, productId);
        return ResponseEntity.ok(updatedCart);
    }

    // Xoá toàn bộ giỏ
    @DeleteMapping("/all")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        cartService.clearCart(email);
        return ResponseEntity.ok("Cart cleared");
    }
}
