CREATE TABLE users ( // Người dùng
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100),
                       full_name VARCHAR(100),
                       role ENUM('customer', 'admin') DEFAULT 'customer',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE categories ( // Danh mục laptop
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT
);
CREATE TABLE products (  // Thông tin laptop
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          category_id INT,
                          brand VARCHAR(100),
                          price DECIMAL(12,2) NOT NULL,
                          description TEXT,
                          image_url VARCHAR(255),
                          stock_quantity INT DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE TABLE orders ( // Đơn đặt hàng
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total_amount DECIMAL(12,2),
                        status ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending',
                        FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE order_items ( // Chi tiết sản phẩm trong đơn hàng
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id INT,
                             product_id INT,
                             quantity INT,
                             price DECIMAL(12,2),
                             FOREIGN KEY (order_id) REFERENCES orders(id),
                             FOREIGN KEY (product_id) REFERENCES products(id)
);
