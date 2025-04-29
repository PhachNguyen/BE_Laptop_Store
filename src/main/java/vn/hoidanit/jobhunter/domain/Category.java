    package vn.hoidanit.jobhunter.domain;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.*;
    import com.fasterxml.jackson.annotation.JsonIgnore;


    import java.util.List;

    @Entity
    @Table(name = "categories")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class Category {

        @Id
        private String id; // ví dụ: "electronics", "fashion"

        private String label; // ví dụ: "Điện tử", "Thời trang"

        //     Một loại có nhiều sản phẩm
    //    mapped : Product là chủ sở hữa quan hệ còn category là phía tham chiếu
        @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<Product> products; // Quan hệ ngược
    }
