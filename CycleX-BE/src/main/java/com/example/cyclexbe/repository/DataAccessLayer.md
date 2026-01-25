# Repository Layer (Data Access Layer)

## 🎯 Chức năng
Cung cấp các cơ chế để truy cập, tìm kiếm và thay đổi dữ liệu trong Database.

## 📝 Nguyên tắc
- Thường là các Interface kế thừa từ `JpaRepository`.
- Tận dụng sức mạnh của Spring Data JPA để viết các phương thức truy vấn nhanh (Query Methods).
- Chỉ tập trung vào việc lấy dữ liệu, không xử lý logic.

## 🛠 Thường dùng
- `@Repository`, `extends JpaRepository<Entity, ID>`, `@Query`.
