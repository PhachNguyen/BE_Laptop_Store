package vn.hoidanit.jobhunter.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
//import vn.hoidanit.jobhunter.domain.dto.CategoryDeserializer;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Entity
@Table(name = "products")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private int stockQuantity;
    private String brand;
    private String warranty;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    //    fetch : LAZY chỉ khi gọi method thì mới hiển thị
//     EAGER   : Khi load product sẽ kèm theo Category tương ứng
//@JsonDeserialize(using = CategoryDeserializer.class)
// Vậy BE sẽ hiểu: "category": "laptop" → ánh xạ thành Category có id = "laptop"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Object

    //    Khác với Many-to-one là sẽ init table
    @ElementCollection // Lưu danh sách các giá trị cơ bản
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id")) // Tạo một bảng riêng
    @Column(name = "image_url") // Chỉ định tên bảng phụ
    private List<String> images;


    // Set Create
    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    // Set Update
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}