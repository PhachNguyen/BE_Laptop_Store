package vn.hoidanit.jobhunter.domain.request;


import lombok.Data;

@Data
public class PaymentCallbackRequest {
    private Long orderId;     // ID của đơn hàng
    private boolean success;  // true = thanh toán thành công, false = thất bại
}