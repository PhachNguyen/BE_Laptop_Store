package vn.hoidanit.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Order;
import vn.hoidanit.jobhunter.domain.request.OrderRequest;
import vn.hoidanit.jobhunter.domain.request.PaymentCallbackRequest;
import vn.hoidanit.jobhunter.domain.response.PaymentUrlResponse;
import vn.hoidanit.jobhunter.service.OrderService;
import vn.hoidanit.jobhunter.util.constant.PaymentStatus;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 📦 Lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 📦 Lấy chi tiết đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 📦 Lọc đơn hàng theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    // 📦 Huỷ đơn hàng
    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Đơn hàng đã được huỷ thành công!");
    }

    // 📦 Tạo đơn hàng mới + trả payment URL
    @PostMapping("/create-payment")
    public ResponseEntity<PaymentUrlResponse> createPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody OrderRequest request
    ) throws UnsupportedEncodingException {
        if (jwt == null) {
            return ResponseEntity.status(401).build(); // Không có token
        }
        String email = jwt.getSubject(); // Lấy email từ JWT
        String paymentUrl = orderService.createOrderFromCart(email, request);
        return ResponseEntity.ok(new PaymentUrlResponse(paymentUrl));
    }
// Tạo mà k tích hợp API VNPay
    @PostMapping("/create-payment_")
    public ResponseEntity<String> createOrder(@AuthenticationPrincipal Jwt jwt, @RequestBody OrderRequest request) throws UnsupportedEncodingException {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String email = jwt.getSubject();
        orderService.createOrderFromCart(email, request); // chỉ tạo đơn, không sinh paymentUrl
        return ResponseEntity.ok("Order created successfully");
    }

    // 📦 Nhận callback thanh toán để cập nhật đơn
    @PostMapping("/payment-callback")
    public ResponseEntity<Void> handlePaymentCallback(@RequestBody PaymentCallbackRequest request) {
        orderService.updatePaymentStatus(request.getOrderId(), request.isSuccess());
        return ResponseEntity.ok().build();
    }
}
