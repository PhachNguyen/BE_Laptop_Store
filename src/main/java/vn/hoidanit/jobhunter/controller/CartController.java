package vn.hoidanit.jobhunter.controller;

import jakarta.transaction.Transactional;
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

    // Thêm sản phẩm vào giỏ - nhận luôn quantity
    @PostMapping("/add")
    public ResponseEntity<List<CartItem>> addToCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> body // Đổi từ Long -> Object để nhận được quantity
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        Long productId = Long.valueOf(body.get("productId").toString());
        // Nhận quantity từ FE, nếu không có thì mặc định là 1
        Integer quantity = body.get("quantity") != null ? Integer.valueOf(body.get("quantity").toString()) : 1;
        List<CartItem> updatedCart = cartService.addToCart(email, productId, quantity);
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
    // Xóa một sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<List<CartItem>> removeFromCart(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        List<CartItem> updatedCart = cartService.removeFromCart(email, productId);
        return ResponseEntity.ok(updatedCart);
    }
// Update
// Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<List<CartItem>> updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> body
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        Long productId = ((Number)body.get("productId")).longValue();
        Integer quantity = ((Number)body.get("quantity")).intValue();

        List<CartItem> updatedCart = cartService.updateQuantity(email, productId, quantity);
        return ResponseEntity.ok(updatedCart);
}

}
