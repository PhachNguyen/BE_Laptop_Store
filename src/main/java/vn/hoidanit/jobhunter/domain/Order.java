package vn.hoidanit.jobhunter.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.PaymentStatus;
import vn.hoidanit.jobhunter.util.constant.ShippingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Orders")
    public class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        private User user; // lưu lại user mua hàng

        private String customerName;
        private String customerAddress;
        private String customerPhone;
        private String customerEmail;
        private String paymentMethod;
        private String shippingMethod;
        private Long shippingFee;
        private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // đã tạo enum trước đó
    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus = ShippingStatus.PACKING; // mặc định

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}

