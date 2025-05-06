package vn.hoidanit.jobhunter.domain.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String paymentMethod;
    private String shippingMethod;
    private Long shippingFee;
    private Long totalAmount;
    private String paymentStatus;
    private String shippingStatus;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
}