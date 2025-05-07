package vn.hoidanit.jobhunter.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "Reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;         // Số sao
    private String review;     // Nội dung
    private Instant createdAt;
    @Column(name = "admin_reply")
    private String adminReply; // Câu trả lời của admin
    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;
    // ...get/set
}

