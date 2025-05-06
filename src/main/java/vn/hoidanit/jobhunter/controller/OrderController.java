package vn.hoidanit.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Order;
import vn.hoidanit.jobhunter.domain.dto.OrderDTO;
import vn.hoidanit.jobhunter.domain.request.OrderRequest;
import vn.hoidanit.jobhunter.domain.request.PaymentCallbackRequest;

import vn.hoidanit.jobhunter.domain.response.PaymentUrlResponse;
import vn.hoidanit.jobhunter.service.OrderService;
import vn.hoidanit.jobhunter.util.constant.PaymentStatus;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        Page<Order> pagedOrders = orderService.getOrdersByUser(email, page, size, status, fromDate, toDate);
        Page<OrderDTO> dtoPage = pagedOrders.map(orderService::convertToDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<OrderDTO> getOrderDetailForUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        if (jwt == null) return ResponseEntity.status(401).build();
        String email = jwt.getSubject();
        Order order = orderService.getOrderByIdForUser(id, email);
        OrderDTO dto = orderService.convertToDTO(order);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Đơn hàng đã được huỷ thành công!");
    }

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentUrlResponse> createPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody OrderRequest request
    ) throws UnsupportedEncodingException {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        String email = jwt.getSubject();
        String paymentUrl = orderService.createOrderFromCart(email, request);
        return ResponseEntity.ok(new PaymentUrlResponse(paymentUrl));
    }

    @PostMapping("/payment-callback")
    public ResponseEntity<Order> handlePaymentCallback(@RequestBody PaymentCallbackRequest request) {
        Order updatedOrder = orderService.updatePaymentStatus(request.getOrderId(), request.isSuccess());
        return ResponseEntity.ok(updatedOrder);
    }
}
