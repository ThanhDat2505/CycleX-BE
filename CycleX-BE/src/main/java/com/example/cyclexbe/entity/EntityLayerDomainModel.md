# Entity Layer (Domain Model)

## 🎯 Chức năng
Đại diện cho các bảng (tables) trong cơ sở dữ liệu. Mỗi class trong folder này tương ứng với một thực thể thực tế.

## 📝 Nguyên tắc
- Ánh xạ trực tiếp với cấu trúc Database.
- Chứa các mối quan hệ giữa các thực thể (One-to-One, One-to-Many, Many-to-Many).
- Nên hạn chế trả về trực tiếp Entity cho Client (nên qua DTO).

## 🛠 Thường dùng
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`,...
