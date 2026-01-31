# Batch 1 Update - Validation DTOs Implementation

## 📝 Thay Đổi Chính

### 1. **SellerController** - Endpoint Updates
Tất cả endpoints trong Batch 1 đã được chỉnh sửa để sử dụng `@Valid @RequestBody` validation:

#### Trước (Header-based):
```java
@GetMapping("/dashboard/stats")
public ResponseEntity<SellerDashboardStatsResponse> getDashboardStats(
        @RequestHeader("X-Seller-Id") Integer sellerId) { ... }
```

#### Sau (DTO-based validation):
```java
@GetMapping("/dashboard/stats")
public ResponseEntity<SellerDashboardStatsResponse> getDashboardStats(
        @Valid @RequestBody GetDashboardStatsRequest req) { ... }
```

---

### 2. **DTOs Tạo Mới** (4 files)

#### a) **GetDashboardStatsRequest**
```java
public class GetDashboardStatsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;
}
```
**Endpoint:** `GET /api/seller/dashboard/stats`

---

#### b) **GetListingsRequest**
```java
public class GetListingsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    public String status;
    public String sort;
    
    @Min(value = 0, message = "Page must be >= 0")
    public Integer page = 0;
    
    @Min(value = 1, message = "Page size must be >= 1")
    public Integer pageSize = 10;
}
```
**Endpoint:** `POST /api/seller/listings/search` (changed from GET)

---

#### c) **GetListingDetailRequest**
```java
public class GetListingDetailRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    @NotNull(message = "Listing ID is required")
    public Integer listingId;
}
```
**Endpoints:** 
- `POST /api/seller/listings/detail` (changed from GET)
- `POST /api/seller/listings/rejection` (changed from GET)

---

#### d) **UpdateListingRequest**
```java
public class UpdateListingRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    @Size(max = 255, message = "Title must be <= 255 characters")
    public String title;

    public String description;
    
    @Size(max = 50)
    public String bikeType;
    
    @Size(max = 100)
    public String brand;
    
    @Size(max = 100)
    public String model;
    
    public Integer manufactureYear;
    
    @Size(max = 50)
    public String condition;
    
    @Size(max = 100)
    public String usageTime;
    
    public String reasonForSale;
    public BigDecimal price;
    
    @Size(max = 100)
    public String locationCity;
    
    public String pickupAddress;
    public BikeListingStatus status;
}
```
**Endpoint:** `PATCH /api/seller/listings/{listing_id}`

---

## 🔄 API Endpoint Changes

| Endpoint | Phương Pháp Cũ | Phương Pháp Mới | Request Body |
|----------|-------|-------|--------------|
| Dashboard Stats | `GET` + Header | `GET` + Body | `GetDashboardStatsRequest` |
| My Listings | `GET` + Params | `POST` | `GetListingsRequest` |
| Listing Detail | `GET` + Header | `POST` | `GetListingDetailRequest` |
| Rejection Reason | `GET` + Header | `POST` | `GetListingDetailRequest` |
| Update Listing | `PATCH` | `PATCH` | `UpdateListingRequest` |

---

## ✅ Validation Rules

### GetDashboardStatsRequest
- `sellerId`: ✅ @NotNull

### GetListingsRequest
- `sellerId`: ✅ @NotNull
- `page`: ✅ @Min(0)
- `pageSize`: ✅ @Min(1)
- `status`: Optional (validate trong service)
- `sort`: Optional

### GetListingDetailRequest
- `sellerId`: ✅ @NotNull
- `listingId`: ✅ @NotNull

### UpdateListingRequest
- `sellerId`: ✅ @NotNull
- `title`: ✅ @Size(max=255)
- `bikeType`: ✅ @Size(max=50)
- `brand`: ✅ @Size(max=100)
- `model`: ✅ @Size(max=100)
- `condition`: ✅ @Size(max=50)
- `usageTime`: ✅ @Size(max=100)
- `locationCity`: ✅ @Size(max=100)

---

## 📊 Build Status
✅ **Maven Compile: SUCCESS**
- **43 source files** compiled (từ 39 → 43: +4 DTO files)
- No errors, no warnings

---

## 🧪 Test Examples

### Test 1: Dashboard Stats
```bash
POST /api/seller/dashboard/stats
Content-Type: application/json

{
  "sellerId": 1
}

Response: 200 OK
{
  "approvedCount": 2,
  "pendingCount": 1,
  "rejectedCount": 1,
  "totalListings": 4,
  "totalViews": 45
}
```

### Test 2: My Listings (with validation)
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "status": "APPROVED",
  "sort": "newest",
  "page": 0,
  "pageSize": 10
}

Response: 200 OK
{
  "content": [...],
  "totalElements": 2,
  ...
}
```

### Test 3: Validation Error Example
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "page": -1,        # Invalid: must be >= 0
  "pageSize": 0      # Invalid: must be >= 1
}

Response: 400 BAD_REQUEST
{
  "errors": [
    "sellerId: Seller ID is required",
    "page: Page must be >= 0",
    "pageSize: Page size must be >= 1"
  ]
}
```

---

## ⚠️ Breaking Changes (for Frontend)

### Before (Header-based):
```javascript
// GET /api/seller/dashboard/stats
headers: { "X-Seller-Id": 1 }
```

### After (Body-based):
```javascript
// GET /api/seller/dashboard/stats
{
  "sellerId": 1
}
```

---

## 📌 Notes
1. Tất cả POST requests đều validate bằng `@Valid` annotation
2. Error responses sẽ return 400 BAD_REQUEST với validation messages
3. Framework sẽ tự động reject requests nếu validation fail
4. Tất cả sellerId fields là required để maintain security

---

## ✨ Lợi Ích
✅ Validation at controller level  
✅ Type-safe requests  
✅ Clear error messages  
✅ Better documentation (DTOs show required fields)  
✅ Consistent API design  
