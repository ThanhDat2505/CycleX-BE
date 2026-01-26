# Exception Handling

## 🎯 Chức năng
Quản lý các lỗi và ngoại lệ trong toàn bộ ứng dụng một cách tập trung.

## 📝 Thành phần
- **Custom Exceptions**: Các class lỗi tự định nghĩa (ví dụ: `ResourceNotFoundException`).
- **Global Exception Handler**: Sử dụng `@ControllerAdvice` để bắt lỗi và trả về thông báo lỗi nhất quán cho Client dưới dạng JSON thay vì trả về stack trace loằng ngoằng.

## 🛠 Thường dùng
- `@ControllerAdvice`, `@ExceptionHandler`, `@ResponseStatus`.
