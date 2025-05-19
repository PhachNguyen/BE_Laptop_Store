package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter

public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<String> images;
}
