package vn.hoidanit.jobhunter.domain.request;

import lombok.Data;

@Data
public class OrderRequest {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String shippingMethod;
    private String paymentMethod; // "cod" hoặc "vnpay"
    private Long shippingFee;
}
