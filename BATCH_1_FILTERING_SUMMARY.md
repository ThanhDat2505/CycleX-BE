# Batch 1 - Enhanced Filtering Implementation

## 📝 Chỉnh Sửa & Thêm Mới

### 1. **BikeListingRepository** - Thêm JpaSpecificationExecutor
```java
public interface BikeListingRepository extends JpaRepository<BikeListing, Integer>, 
        JpaSpecificationExecutor<BikeListing> { ... }
```
**Lợi ích:** Hỗ trợ dynamic filtering với multiple conditions qua Specification pattern

---

### 2. **GetListingsRequest DTO** - Cập nhật Filter Fields
```java
public class GetListingsRequest {
    @NotNull(message = "Seller ID is required")
    public Integer sellerId;

    // Filter by status
    public String status;
    
    // Sort order
    public String sort;
    
    // Filter by text fields (case-insensitive)
    public String title;      // ✅ NEW
    public String brand;      // ✅ NEW
    public String model;      // ✅ NEW
    
    // Filter by price range ✅ NEW
    public BigDecimal minPrice;
    public BigDecimal maxPrice;

    // Pagination
    @Min(value = 0)
    public Integer page = 0;
    
    @Min(value = 1)
    public Integer pageSize = 10;
}
```

---

### 3. **SellerService** - Dynamic Filtering với Specification

#### Updated: `getSellerListings()` Method
```java
public Page<SellerListingResponse> getSellerListings(Integer sellerId, String status, 
                                                     String title, String brand, 
                                                     String model, BigDecimal minPrice, 
                                                     BigDecimal maxPrice, String sort, 
                                                     int page, int pageSize)
```

#### New: `buildSellerListingSpec()` Method
```java
private Specification<BikeListing> buildSellerListingSpec(User seller, String status, 
                                                          String title, String brand,
                                                          String model, BigDecimal minPrice, 
                                                          BigDecimal maxPrice)
```

**Criteria quản lý:**
- ✅ **Seller Filter** (Required): Chỉ show listing của seller
- ✅ **Status Filter** (Optional): APPROVED, PENDING, REJECTED
- ✅ **Title Filter** (Optional): Case-insensitive LIKE search
- ✅ **Brand Filter** (Optional): Case-insensitive LIKE search
- ✅ **Model Filter** (Optional): Case-insensitive LIKE search
- ✅ **Price Range Filter** (Optional): minPrice ≤ price ≤ maxPrice

---

### 4. **SellerController** - Update getListings()
```java
@PostMapping("/listings/search")
public ResponseEntity<Page<SellerListingResponse>> getListings(
        @Valid @RequestBody GetListingsRequest req) {
    Page<SellerListingResponse> listings = sellerService.getSellerListings(
            req.sellerId, req.status, req.title, req.brand, req.model, 
            req.minPrice, req.maxPrice, req.sort, req.page, req.pageSize);
    return ResponseEntity.ok(listings);
}
```

---

## 🧪 Test Examples

### Test 1: Search by Title
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "title": "Honda",
  "page": 0,
  "pageSize": 10
}

Response: 200 OK
{
  "content": [
    {
      "listingId": 1,
      "title": "Honda CB 150R",
      "brand": "Honda",
      "price": 45000000,
      "status": "APPROVED"
    },
    ...
  ],
  "totalElements": 5,
  "totalPages": 1
}
```

### Test 2: Search by Brand
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "brand": "Yamaha",
  "page": 0,
  "pageSize": 10
}

Response: 200 OK
{ ... listings with brand="Yamaha" ... }
```

### Test 3: Search by Model
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "model": "2023",
  "page": 0,
  "pageSize": 10
}

Response: 200 OK
{ ... listings with model containing "2023" ... }
```

### Test 4: Search by Price Range
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "minPrice": 30000000,
  "maxPrice": 50000000,
  "page": 0,
  "pageSize": 10
}

Response: 200 OK
{ ... listings with 30M ≤ price ≤ 50M ... }
```

### Test 5: Combined Filters
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "status": "APPROVED",
  "brand": "Honda",
  "minPrice": 20000000,
  "maxPrice": 60000000,
  "sort": "newest",
  "page": 0,
  "pageSize": 5
}

Response: 200 OK
{
  ... listings where:
  - seller = 1
  - status = APPROVED
  - brand contains "Honda"
  - 20M ≤ price ≤ 60M
  - sorted by createdAt DESC
  - paginated by 5 per page ...
}
```

### Test 6: Validation Error (invalid page)
```bash
POST /api/seller/listings/search
Content-Type: application/json

{
  "sellerId": 1,
  "page": -1,
  "pageSize": 0
}

Response: 400 BAD_REQUEST
{
  "errors": [
    "page: Page must be >= 0",
    "pageSize: Page size must be >= 1"
  ]
}
```

---

## 🔄 Database Query Flow

```
getListings(req)
  ↓
buildSellerListingSpec()
  ↓ (Criteria Builder)
SELECT * FROM bike_listings 
WHERE seller_id = ?
  AND (status = ? OR status IS NOT NULL)
  AND (LOWER(title) LIKE ? OR title IS NULL)
  AND (LOWER(brand) LIKE ? OR brand IS NULL)
  AND (LOWER(model) LIKE ? OR model IS NULL)
  AND (price >= ? OR price IS NULL)
  AND (price <= ? OR price IS NULL)
ORDER BY created_at DESC
LIMIT ? OFFSET ?
```

---

## ✅ Build Status
✅ **Maven Compile: SUCCESS**
- 43 source files compiled
- No errors, no warnings

---

## 📊 Features Summary

| Feature | Tên | Type | Notes |
|---------|------|------|-------|
| Seller Filter | Required | String→Integer | Mandatory for security |
| Status Filter | status | Optional Enum | APPROVED, PENDING, REJECTED |
| Title Search | title | Optional String | Case-insensitive LIKE |
| Brand Search | brand | Optional String | Case-insensitive LIKE |
| Model Search | model | Optional String | Case-insensitive LIKE |
| Min Price | minPrice | Optional BigDecimal | price >= minPrice |
| Max Price | maxPrice | Optional BigDecimal | price <= maxPrice |
| Sort | sort | Optional String | "newest" (DESC) or "oldest" (ASC) |
| Page | page | Optional Integer | 0-indexed, default 0 |
| Page Size | pageSize | Optional Integer | Items per page, default 10 |

---

## 💡 Improvements Made

✅ Dynamic filtering using JPA Criteria API  
✅ Multiple filter conditions combined with AND logic  
✅ Case-insensitive text search  
✅ Price range filtering  
✅ Type-safe filters via DTO  
✅ Automatic validation via @Valid  
✅ Efficient database queries  
✅ Pagination support  
✅ Sort flexibility  

---

## ⏭️ Next Steps

1. Test endpoints with various filter combinations
2. Move to Batch 2: Create Listing + Submit for Approval
3. Add image upload support (Batch 4)
