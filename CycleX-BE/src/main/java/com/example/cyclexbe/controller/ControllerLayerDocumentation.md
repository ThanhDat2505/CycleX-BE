# Controller Layer (Web Layer)

## 🎯 Chức năng
Đây là tầng giao tiếp trực tiếp với Client. Nhiệm vụ chính là tiếp nhận các yêu cầu HTTP (GET, POST, PUT, DELETE) và điều hướng chúng.

## 📝 Nguyên tắc
- Chỉ xử lý điều hướng và kiểm tra dữ liệu đầu vào cơ bản.
- **Không** viết logic nghiệp vụ (business logic) tại đây.
- Gọi các phương thức từ tầng `Service` để xử lý dữ liệu.
- Trả về `ResponseEntity` kèm theo dữ liệu (DTO) và HTTP Status Code phù hợp.

## 🛠 Thường dùng
- `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`,...
