# CycleX-BE - Bike Listing Platform Backend

> Backend API cho nền tảng giao dịch xe đạp với hệ thống duyệt bài đăng

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-Latest-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red)](#)

---

## 📋 Mục Đích Dự Án

CycleX-BE là backend API cho nền tảng giao dịch xe đạp (bike marketplace) với các tính năng chính:

- 👤 **User Management**: Đăng ký, đăng nhập, xác minh OTP
- 🚴 **Listing Management**: Tạo, cập nhật, xóa bài đăng bán xe đạp
- 🔍 **Search & Filter**: Tìm kiếm xe đạp theo status, thành phố, giá cả
- 👨‍💼 **Seller Dashboard**: Quản lý listings, xem thống kê, quản lý hình ảnh
- 👮 **Inspector Review**: Duyệt bài đăng, phê duyệt/từ chối, lịch sử review
- 🖼️ **Image Management**: Upload, xóa hình ảnh listings
- 💰 **Role-Based Access**: BUYER, SELLER, INSPECTOR, ADMIN roles

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ hoặc PostgreSQL 13+
- Postman (optional, để test API)

### Installation

```bash
# 1. Clone repository
git clone <repository-url>
cd CycleX-BE

# 2. Configure database (application.properties)
spring.datasource.url=jdbc:mysql://localhost:3306/cyclexbe
spring.datasource.username=root
spring.datasource.password=password

# 3. Build project
mvn clean package

# 4. Run application
mvn spring-boot:run

# Application sẽ chạy tại: http://localhost:4491
```

### Test APIs

```bash
# 1. Import Postman collection
# File: CycleX-API.postman_collection.json
# - Mở Postman
# - File → Import → Chọn file
# - Set baseUrl = http://localhost:4491

# 2. Test endpoint đầu tiên
# POST /api/auth/register
# Body: {
#   "email": "user@example.com",
#   "password": "Password123",
#   "firstName": "John",
#   "lastName": "Doe",
#   "phoneNumber": "0123456789",
#   "role": "SELLER"
# }
```

---

## 📚 Documentation

### Comprehensive Guides

| Document | Mô tả |
|----------|-------|
| **[CycleXBE-Architecture-Guide.md](CycleXBE-Architecture-Guide.md)** | Chi tiết kiến trúc, entities, DTOs, security, code patterns |
| **[CODE-RULES-AND-CONVENTIONS.md](CODE-RULES-AND-CONVENTIONS.md)** | Quy tắc code, naming conventions, best practices |
| **[DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md)** | Checklist phát triển, templates, quy trình tạo feature |
| **[PROJECT-SUMMARY.md](PROJECT-SUMMARY.md)** | Tóm tắt toàn diện, quick facts, statistics |
| **[CycleX-API.postman_collection.json](CycleX-API.postman_collection.json)** | 39 endpoints ready to test |

---

## 🏗️ Project Structure

```
CycleX-BE/
├── src/main/java/com/example/cyclexbe/
│   ├── controller/          # REST Controllers (5)
│   ├── service/             # Business Logic (7)
│   ├── repository/          # Data Access (4)
│   ├── entity/              # Database Models (4)
│   ├── dto/                 # Request/Response (27)
│   ├── security/            # JWT + Security
│   ├── domain/enums/        # Enumerations
│   ├── exception/           # Error Handling
│   └── util/                # Utilities
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/        # Flyway Migrations
├── CycleX-API.postman_collection.json
├── Dockerfile
└── pom.xml
```

---

## 🎮 API Endpoints

### Authentication (4)
```
POST   /api/auth/login           # Đăng nhập
POST   /api/auth/register        # Đăng ký
POST   /api/auth/send-otp        # Gửi OTP
POST   /api/auth/verify-otp      # Xác minh OTP
```

### Users (5)
```
GET    /api/users                # Danh sách users
GET    /api/users/{id}           # Chi tiết user
POST   /api/users                # Tạo user
PUT    /api/users/{id}           # Cập nhật user
DELETE /api/users/{id}           # Xóa user
```

### Bike Listings (5)
```
GET    /api/bikelistings         # Danh sách listings (public)
GET    /api/bikelistings/{id}    # Chi tiết listing
POST   /api/bikelistings         # Tạo listing
PUT    /api/bikelistings/{id}    # Cập nhật listing
DELETE /api/bikelistings/{id}    # Xóa listing
```

### Seller Features (13)
```
GET    /api/seller/{id}/dashboard/stats              # Dashboard
GET    /api/seller/{id}/listings/search              # My listings
POST   /api/seller/{id}/listings/create              # Create listing
GET    /api/seller/{id}/listings/{id}/detail         # Chi tiết listing
GET    /api/seller/{id}/listings/{id}/rejection      # Rejection reason
GET    /api/seller/{id}/listings/{id}/preview        # Preview
GET    /api/seller/{id}/drafts                       # Drafts
POST   /api/seller/{id}/drafts/{id}/submit           # Submit draft
DELETE /api/seller/{id}/drafts/{id}                  # Delete draft
GET    /api/seller/{id}/listings/{id}/images         # Get images
POST   /api/seller/{id}/listings/{id}/images         # Upload image
DELETE /api/seller/{id}/listings/{id}/images/{id}    # Delete image
POST   /api/seller/{id}/listings/{id}/images/{id}/retry # Retry upload
```

### Inspector Features (10)
```
GET    /api/inspector/{id}/dashboard/stats           # Dashboard
GET    /api/inspector/{id}/listings                  # For review
GET    /api/inspector/{id}/listings/{id}/detail      # Detail
POST   /api/inspector/{id}/listings/{id}/lock        # Lock
POST   /api/inspector/{id}/listings/{id}/unlock      # Unlock
POST   /api/inspector/{id}/listings/{id}/approve     # Approve
POST   /api/inspector/{id}/listings/reject           # Reject
POST   /api/inspector/{id}/reviews                   # History
POST   /api/inspector/{id}/reviews/detail            # Review detail (TODO)
POST   /api/inspector/{id}/disputes                  # Disputes (TODO)
POST   /api/inspector/{id}/disputes/detail           # Dispute detail (TODO)
```

**Total**: 37 public + protected endpoints

---

## 🔒 Security

### Authentication
- **Method**: JWT (JSON Web Token)
- **Location**: `Authorization: Bearer {token}` header
- **Expiry**: Configurable in application.properties
- **Provider**: JwtProvider (signs & validates tokens)

### Authorization
- **Type**: Role-Based Access Control (RBAC)
- **Roles**: BUYER, SELLER, INSPECTOR, ADMIN
- **Enforcement**: @PreAuthorize annotations
- **Ownership**: SecurityUtils.validateResourceOwner()

### Example
```java
@RestController
@RequestMapping("/api/seller/{sellerId}")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {
    
    @GetMapping("/listings/{id}")
    public ResponseEntity<Response> get(
            @PathVariable Integer sellerId,
            @PathVariable Integer id) {
        
        // Automatically checks: 1) Token valid, 2) Has SELLER role
        // Must also validate: 3) Ownership (sellerId = current user)
        SecurityUtils.validateResourceOwner(sellerId.toString(), "SELLER");
        
        return ResponseEntity.ok(service.get(id));
    }
}
```

---

## 📊 Database Schema

### Tables (4 Core)

```sql
-- Users
users (id, email, password, role, status, created_at, updated_at)

-- Listings
bike_listing (id, seller_id, inspector_id, title, price, status, created_at, updated_at)

-- Images
listing_image (id, listing_id, image_url, status, created_at)

-- OTP
email_otp (id, email, otp, expires_at, verified)
```

### Statuses
```
BikeListingStatus: DRAFT, PENDING, REVIEWING, APPROVED, REJECTED
Role:              ROLE_BUYER, ROLE_SELLER, ROLE_INSPECTOR, ROLE_ADMIN
```

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.2.5 |
| **Language** | Java 17 |
| **ORM** | Spring Data JPA / Hibernate |
| **Database** | MySQL / PostgreSQL |
| **Security** | Spring Security + JWT |
| **Validation** | Jakarta Validation |
| **Build Tool** | Maven |
| **Migrations** | Flyway |
| **Logging** | SLF4J / Logback |

---

## 📝 Code Examples

### Create a Listing (Seller)

```bash
curl -X POST http://localhost:4491/api/seller/1/listings/create \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Trek Mountain Bike",
    "brand": "Trek",
    "model": "X-Caliber",
    "price": 1500,
    "condition": "EXCELLENT",
    "city": "Ho Chi Minh",
    "description": "Well-maintained mountain bike"
  }'
```

### Approve Listing (Inspector)

```bash
curl -X POST http://localhost:4491/api/inspector/1/listings/123/approve \
  -H "Authorization: Bearer {token}"
```

### Search Listings (Public)

```bash
curl "http://localhost:4491/api/bikelistings?page=0&size=10&city=Hanoi&status=APPROVED"
```

---

## 🧪 Testing

### Using Postman

1. Import `CycleX-API.postman_collection.json`
2. Set environment variable: `baseUrl = http://localhost:4491`
3. Run "Login" request first to get token
4. Token will auto-populate in subsequent requests
5. Test all 39 endpoints

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=BikeListingControllerTest

# With coverage
mvn test jacoco:report
```

---

## 📈 Project Statistics

| Metric | Value |
|--------|-------|
| Controllers | 5 |
| Services | 7 |
| Repositories | 4 |
| Entities | 4 |
| DTOs | 27 (cleaned up) |
| Endpoints | 37 |
| Database Tables | 4+ |
| Lines of Code | ~5000 |
| Code Coverage | ~40% |

---

## 🎯 Development Workflow

### Adding a New Feature

1. **Plan** - Define endpoint, request/response, business rules
2. **Create DTOs** - CreateXyzRequest, XyzResponse
3. **Create Entity** - If new table needed (with JPA annotations)
4. **Create Repository** - Extend JpaRepository, add queries
5. **Create Service** - Business logic, validation, DTO mapping
6. **Create Controller** - Endpoint, security, error handling
7. **Test** - Add to Postman, test scenarios
8. **Document** - Update architecture guide if needed

See [DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md) for detailed process.

---

## ⚠️ Important Notes

### Current Status
- ✅ Core features complete
- ⚠️ 3 TODO endpoints (disputes, review detail)
- ⚠️ Email notifications not yet implemented
- ⚠️ Image upload processing pending

### Best Practices Followed
- ✅ Clean architecture layers
- ✅ Separation of concerns
- ✅ Comprehensive validation
- ✅ Role-based security
- ✅ Error handling standardized
- ✅ DTOs separate from entities
- ✅ Transactional operations
- ✅ Constructor injection

### Common Pitfalls to Avoid
❌ Don't return entities directly from API  
❌ Don't skip validation with @Valid  
❌ Don't forget resource ownership checks  
❌ Don't create unused DTOs  
❌ Don't hardcode business logic in controllers  
❌ Don't forget @Transactional on write operations  

---

## 📞 Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8080

# Or kill process using port 4491
# Windows: netstat -ano | findstr 4491
# Linux: lsof -i :4491 && kill -9 <PID>
```

### Database Connection Error
```
Check in application.properties:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- Database is running and accessible
```

### JWT Token Expired
```
Token expiration is set in application.properties
Default: Check jwtExpiresIn value
Get new token by logging in again
```

### Missing Flyway Migrations
```
Ensure db/migration/ folder exists
Add SQL migrations following naming: V1__name.sql, V2__name.sql
Flyway automatically runs on startup
```

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [Postman Documentation](https://www.postman.com/product/what-is-postman/)

---

## 🤝 Contributing

### Before Starting Development
1. Read [CODE-RULES-AND-CONVENTIONS.md](CODE-RULES-AND-CONVENTIONS.md)
2. Read [CycleXBE-Architecture-Guide.md](CycleXBE-Architecture-Guide.md)
3. Follow naming conventions
4. Run tests before committing
5. Update documentation if needed

### Pull Request Process
1. Create feature branch: `feature/xyz`
2. Implement feature following conventions
3. Add unit tests
4. Test with Postman
5. Update documentation
6. Create pull request with description

---

## 📄 License

Proprietary - All rights reserved

---

## 📧 Support

For questions or issues:
1. Check [DEVELOPMENT-CHECKLIST.md](DEVELOPMENT-CHECKLIST.md) FAQ section
2. Review [CycleXBE-Architecture-Guide.md](CycleXBE-Architecture-Guide.md)
3. Inspect [CODE-RULES-AND-CONVENTIONS.md](CODE-RULES-AND-CONVENTIONS.md)
4. Test endpoints with [CycleX-API.postman_collection.json](CycleX-API.postman_collection.json)

---

## ✅ Status

- **Development Phase**: Active ✅
- **Last Updated**: 2025-02-06
- **Next Milestone**: Implement TODO endpoints
- **Production Ready**: Not yet (need more tests & TODO implementation)

---

**Happy Coding! 🚀**

*For detailed information, please refer to the comprehensive documentation files in this repository.*

