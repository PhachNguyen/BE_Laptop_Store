package vn.hoidanit.jobhunter.domain.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private Long price;
    private String imageUrl;
    private Long productId;
}