# DTO (Data Transfer Object)

## 🎯 Chức năng
Chứa các đối tượng dùng để vận chuyển dữ liệu giữa các lớp (layers) và giữa hệ thống với Client.

## 📝 Nguyên tắc
- Không chứa logic nghiệp vụ, chỉ chứa các thuộc tính, getter, setter.
- Giúp bảo mật hệ thống bằng cách ẩn đi các thông tin nhạy cảm của `Entity`.
- Thường chia làm 2 loại:
    - **Request DTO**: Dữ liệu từ Client gửi lên.
    - **Response DTO**: Dữ liệu hệ thống trả về cho Client.

## 🛠 Thường dùng
- `@Data`, `@Builder` (Lombok) và các Bean Validation (`@NotBlank`, `@Min`,...).
