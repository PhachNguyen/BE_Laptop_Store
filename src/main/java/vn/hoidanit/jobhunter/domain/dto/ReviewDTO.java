    package vn.hoidanit.jobhunter.domain.dto;

    public class ReviewDTO {
        private Long id;
        private String userName;
        private Integer rating;
        private String review;
        private String createdAt;
        private String adminReply; // Trường mới lưu câu trả lời của admin

        // Getter và Setter cho các trường
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getAdminReply() {
            return adminReply;
        }

        public void setAdminReply(String adminReply) {
            this.adminReply = adminReply;
        }
    }
