# Service Layer (Business Logic Layer)

## 🎯 Chức năng
Đây là nơi quan trọng nhất, chứa toàn bộ logic nghiệp vụ (Business Logic) của ứng dụng.

## 📝 Nguyên tắc
- Tiếp nhận yêu cầu từ `Controller`.
- Gọi các `Repository` để lấy hoặc lưu dữ liệu.
- Xử lý tính toán, kiểm tra điều kiện, xử lý lỗi nghiệp vụ.
- Sử dụng `@Transactional` để đảm bảo tính toàn vẹn của dữ liệu khi thao tác với nhiều bảng cùng lúc.

## 🛠 Thường dùng
- `@Service`, `@Transactional`.
