# FUCinemaBookingSystem – Assignment 01 (MSS301)

Hệ thống đặt vé xem phim trực tuyến gồm 3 Microservices và 1 API Gateway với kiến trúc Polyglot Persistence:
- **API Gateway** (`api-gateway`, port 9000, Spring Cloud Gateway Web MVC + OAuth2 Resource Server)
- **Customer Service** (`customer-service`, port 8081, Microsoft SQL Server 2022 + Flyway)
- **Movie Service** (`movie-service`, port 8082, MongoDB 7.0.5 + DataSeeder)
- **Booking Service** (`booking-service`, port 8083, MySQL 8.3.0 + Flyway + OpenFeign)

---

## 1. Thứ tự khởi động hệ thống

### Bước 1: Khởi động 3 Database bằng Docker Compose
```bash
cd Assignment
docker compose up -d
```
Đợi container `cinema-sqlserver` chuyển sang trạng thái `healthy` (khoảng 20-30s), `cinema-sqlserver-init` hoàn tất (Exited 0).

Kiểm tra trạng thái container:
```bash
docker compose ps
```

### Bước 2: Khởi động 4 Microservices (theo thứ tự)
1. **customer-service** (cổng 8081):
   ```bash
   mvn spring-boot:run -f customer-service/pom.xml
   ```
2. **movie-service** (cổng 8082):
   ```bash
   mvn spring-boot:run -f movie-service/pom.xml
   ```
3. **booking-service** (cổng 8083):
   ```bash
   mvn spring-boot:run -f booking-service/pom.xml
   ```
4. **api-gateway** (cổng 9000):
   ```bash
   mvn spring-boot:run -f api-gateway/pom.xml
   ```

---

## 2. Tài khoản kiểm thử (Test Accounts)

| Vai trò | Email | Mật khẩu | Ghi chú |
|---|---|---|---|
| **Admin** | `admin@fucinema.com` | `@@abc123@@` | Cấu hình trong `application.properties` |
| **Customer 1** | `an@gmail.com` | `123456` | ID: 1, Trạng thái: `ACTIVE` |
| **Customer 2** | `binh@gmail.com` | `123456` | ID: 2, Trạng thái: `ACTIVE` |
| **Customer 3** | `chi@gmail.com` | `123456` | ID: 3, Trạng thái: `INACTIVE` (đăng nhập trả về 403) |

---

## 3. Kiểm thử tự động bằng Postman (F11)

1. Import Environment: `Assignment/postman/FUCinema-Local.postman_environment.json`.
2. Import Collection: `Assignment/postman/FUCinemaBookingSystem.postman_collection.json`.
3. Chọn environment `FUCinema-Local`.
4. Mở **Collection Runner**, tích chọn chạy toàn bộ 8 thư mục (01-Auth -> 08-Report), nhấn **Run FUCinemaBookingSystem**.
5. Kết quả mong đợi: **100% Passed (0 Failed)**.
