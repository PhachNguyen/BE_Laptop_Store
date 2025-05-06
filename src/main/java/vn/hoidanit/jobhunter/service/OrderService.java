package vn.hoidanit.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hoidanit.jobhunter.domain.*;
import vn.hoidanit.jobhunter.domain.dto.OrderDTO;
import vn.hoidanit.jobhunter.domain.dto.OrderItemDTO;
import vn.hoidanit.jobhunter.domain.request.OrderRequest;
import vn.hoidanit.jobhunter.repository.CartRepository;
import vn.hoidanit.jobhunter.repository.OrderRepository;

import vn.hoidanit.jobhunter.repository.userRepository;
import vn.hoidanit.jobhunter.util.constant.PaymentStatus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final userRepository userRepository;
    private final PaymentService paymentService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng id = " + id));
    }
    // Tìm đơn hàng theo user
    public Page<Order> getOrdersByUser(String email, int page, int size, String status,
                                       LocalDate fromDate, LocalDate toDate) {
        User user = userRepository.findByEmail(email);
        Pageable pageable = PageRequest.of(page, size);

        // Nếu không lọc thời gian
        if (fromDate == null && toDate == null) {
            if ("all".equalsIgnoreCase(status)) {
                return orderRepository.findByUser(user, pageable);
            } else {
                PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
                return orderRepository.findByUserAndPaymentStatus(user, paymentStatus, pageable);
            }
        }

        // Nếu có lọc thời gian
        LocalDateTime start = fromDate != null ? fromDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime end = toDate != null ? toDate.plusDays(1).atStartOfDay() : LocalDate.MAX.atStartOfDay();

        if ("all".equalsIgnoreCase(status)) {
            return orderRepository.findByUserAndOrderDateBetween(user, start, end, pageable);
        } else {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            return orderRepository.findByUserAndPaymentStatusAndOrderDateBetween(user, paymentStatus, start, end, pageable);
        }
    }


    // Tìm đơn theo ID nhưng kiểm tra quyền sở hữu (chỉ chủ đơn hàng mới được xem)
    public Order getOrderByIdForUser(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng id = " + id));
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này.");
        }
        return order;
    }

    public List<Order> getOrdersByStatus(PaymentStatus status) {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getPaymentStatus() == status)
                .toList();
    }

    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getPaymentStatus() == PaymentStatus.PENDING || order.getPaymentStatus() == PaymentStatus.FAILED) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Không thể huỷ đơn hàng đã thanh toán thành công!");
        }
    }

    public Order updatePaymentStatus(Long orderId, boolean success) {
        Order order = getOrderById(orderId);
        if (success) {
            order.setPaymentStatus(PaymentStatus.SUCCESS);
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
        }
        return orderRepository.save(order);
    }


    @Transactional
    public String createOrderFromCart(String email, OrderRequest request) throws UnsupportedEncodingException {
        User user = userRepository.findByEmail(email);


        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng cho user " + email));

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerAddress(request.getCustomerAddress());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(request.getShippingFee());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());

        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.valueOf(request.getShippingFee()); // sửa thành BigDecimal
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal price = cartItem.getProduct().getPrice(); // Giữ BigDecimal
            orderItem.setPrice(price.longValue()); // ép Long nếu OrderItem yêu cầu

            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity()))); // cộng chính xác

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount.longValue()); // lưu vào DB dạng Long (tổng tiền nguyên)
      order.setOrderItems(orderItems);
      orderRepository.save(order);
  //      Clear cart
       cart.getItems().clear();
      cartRepository.save(cart);

      // Trả về URL thanh toán
//       return paymentService.createPaymentUrl(order);
//       return paymentService.buildPaymentUrl("141",totalAmount,"192.168.1.8");
        if ("vnpay".equalsIgnoreCase(request.getPaymentMethod())) {
            return paymentService.buildPaymentUrl(order.getId().toString(), totalAmount, "192.168.1.8");
        } else {
            return "COD_SUCCESS";
        }

    }

//     Convert sang DTO
public OrderDTO convertToDTO(Order order) {
    OrderDTO dto = new OrderDTO();
    dto.setId(order.getId());
    dto.setCustomerName(order.getCustomerName() != null ? order.getCustomerName() : "Không rõ");
    dto.setCustomerPhone(order.getCustomerPhone() != null ? order.getCustomerPhone() : "...");
    dto.setCustomerEmail(order.getCustomerEmail() != null ? order.getCustomerEmail() : "...");
    dto.setCustomerAddress(order.getCustomerAddress() != null ? order.getCustomerAddress() : "...");

    dto.setPaymentMethod(order.getPaymentMethod());
    dto.setShippingMethod(order.getShippingMethod());
    dto.setShippingFee(order.getShippingFee());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setPaymentStatus(order.getPaymentStatus().name());
    dto.setShippingStatus(
            order.getShippingStatus() != null
                    ? order.getShippingStatus().name()
                    : "UNKNOWN"
    );
    dto.setOrderDate(order.getOrderDate());

    List<OrderItemDTO> itemDTOs = new ArrayList<>();
    if (order.getOrderItems() != null) {
        for (OrderItem item : order.getOrderItems()) {
            if (item == null) continue;
            Product product = item.getProduct();
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductName(product != null ? product.getName() : "Sản phẩm đã xoá");
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());

            if (product != null) {
                itemDTO.setProductId(product.getId()); // ✅ Thêm ID để FE build ảnh
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    itemDTO.setImageUrl(product.getImages().get(0));
                }
            }

            itemDTOs.add(itemDTO);
        }
    }

    dto.setItems(itemDTOs);
    return dto;
}


}
