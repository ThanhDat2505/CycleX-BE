# API Upload Ảnh - Seller Listing Images (S-13)

## Yêu cầu
- FE upload ảnh vào `public` folder riêng của FE
- BE chỉ lưu **path dẫn tới ảnh**, không lưu file ảnh
- Cấu trúc folder: `/public/{listingId}/[image_number].png/jpg`

---

## 1. Upload Ảnh

**Endpoint:** `POST /api/seller/{sellerId}/listings/{listing_id}/images`

**Authorization:** Bearer Token (SELLER role)

**Request Body:**
```json
{
  "imagePath": "/public/5/1.jpg"
}
```

**imagePath Format:**
- Bắt buộc: `/public/{listingId}/[tên_file].ext`
- Extension: `.png`, `.jpg`, `.jpeg`
- Ví dụ: `/public/5/1.jpg`, `/public/5/bike-photo-2.png`

**Response (201 Created):**
```json
{
  "imageId": 1,
  "imagePath": "/public/5/1.jpg",
  "imageOrder": 1,
  "uploadedAt": "2026-02-05T23:55:00"
}
```

**Error Response:**
- `400 Bad Request` - Invalid path format
- `403 Forbidden` - Listing không thuộc seller này
- `404 Not Found` - Listing không tồn tại

---

## 2. Lấy Danh Sách Ảnh

**Endpoint:** `GET /api/seller/{sellerId}/listings/{listing_id}/images`

**Authorization:** Bearer Token (SELLER role)

**Response (200 OK):**
```json
[
  {
    "imageId": 1,
    "imagePath": "/public/5/1.jpg",
    "imageOrder": 1,
    "uploadedAt": "2026-02-05T23:55:00"
  },
  {
    "imageId": 2,
    "imagePath": "/public/5/2.png",
    "imageOrder": 2,
    "uploadedAt": "2026-02-05T23:56:00"
  }
]
```

---

## 3. Xóa Ảnh

**Endpoint:** `DELETE /api/seller/{sellerId}/listings/{listing_id}/images/{image_id}`

**Authorization:** Bearer Token (SELLER role)

**Response (200 OK):**
```json
{
  "message": "Image deleted successfully"
}
```

**Tác dụng sau xóa:**
- Xóa record khỏi DB
- Tự động sắp xếp lại `imageOrder` (1, 2, 3...)
- FE có thể xóa file ảnh từ `/public/{listingId}/` nếu muốn

---

## Flow Hoàn Chỉnh

### Seller Upload Ảnh:

```
1. FE tạo folder: /public/{listingId}/
2. FE upload ảnh vào folder: /public/{listingId}/1.jpg
3. FE gọi API BE:
   POST /api/seller/5/listings/10/images
   Body: { "imagePath": "/public/10/1.jpg" }
4. BE validate path format & listing owner
5. BE lưu vào DB: ListingImage(listing_id=10, imagePath="/public/10/1.jpg", order=1)
6. BE trả về response với imageId
```

### Hiển Thị Ảnh (Frontend):

```
1. FE lấy danh sách ảnh: GET /api/seller/5/listings/10/images
2. Response: [{ imagePath: "/public/10/1.jpg" }, ...]
3. FE render ảnh từ path: <img src="/public/10/1.jpg" />
   (Vì /public là static folder của FE)
```

---

## Database Schema

```sql
CREATE TABLE listing_images (
    image_id SERIAL PRIMARY KEY,
    listing_id INTEGER NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    image_order INTEGER NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id) ON DELETE CASCADE
);
```

---

## Validation Rules

| Field | Rule |
|-------|------|
| `imagePath` | - Bắt buộc<br/>- Format: `/public/{listingId}/xxx.ext`<br/>- Extension: .png, .jpg, .jpeg<br/>- Phải chứa listingId đúng |
| `imageOrder` | - Tự động tăng (1, 2, 3...)<br/>- Tái sắp xếp khi xóa |
| `listing_id` | - Phải tồn tại<br/>- Phải thuộc seller gọi API |

---

## Postman Test

### 1. Upload Ảnh
```
POST http://localhost:8080/api/seller/5/listings/10/images
Authorization: Bearer {token}
Content-Type: application/json

{
  "imagePath": "/public/10/1.jpg"
}
```

### 2. Lấy Ảnh
```
GET http://localhost:8080/api/seller/5/listings/10/images
Authorization: Bearer {token}
```

### 3. Xóa Ảnh
```
DELETE http://localhost:8080/api/seller/5/listings/10/images/1
Authorization: Bearer {token}
```

---

## Notes

- **BE không xử lý file upload**, chỉ lưu path
- **FE quản lý storage** ảnh trong `/public/{listingId}/` folder
- **Automatic reorder** khi xóa ảnh
- **Cascade delete** - xóa listing → xóa tất cả ảnh
- **Max images:** Không giới hạn (có thể cấu hình sau)
