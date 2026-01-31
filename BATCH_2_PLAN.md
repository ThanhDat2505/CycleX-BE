# Batch 2 Plan - Create/Submit Listings & Draft Management

## 🎯 Overview

Batch 2 mở rộng Batch 1 để cho phép sellers:
- ✅ Tạo listing mới (với DRAFT status)
- ✅ Submit listing để duyệt (DRAFT → PENDING)
- ✅ Preview listing trước khi submit
- ✅ Quản lý drafts (view, delete, submit)

---

## 📋 Listing Status Lifecycle

```
DRAFT (created) 
  ↓ (submit)
PENDING (waiting approval) 
  ↓ (admin approves/rejects)
APPROVED or REJECTED
```

---

## 🔄 Endpoints in Batch 2

### **S-12: Create Listing**

#### Endpoint 1: `POST /api/seller/listings` - Create New Listing
```
Method: POST
Path: /api/seller/listings
Status Code: 201 CREATED

Request DTO: CreateListingRequest
- sellerId: Integer @NotNull (required)
- title: String @NotBlank, @Size(max=255) ✅ REQUIRED
- description: String (optional)
- bikeType: String @Size(max=50) ✅ REQUIRED
- brand: String @Size(max=100) ✅ REQUIRED
- model: String @Size(max=100) ✅ REQUIRED
- manufactureYear: Integer (optional)
- condition: String @Size(max=50) (optional)
- usageTime: String @Size(max=100) (optional)
- reasonForSale: String (optional)
- price: BigDecimal @NotNull, @PositiveOrZero ✅ REQUIRED
- locationCity: String @Size(max=100) (optional)
- pickupAddress: String (optional)
- saveDraft: Boolean = true (if true → DRAFT, if false → auto-submit)

Response DTO: BikeListingResponse
- All listing fields
- status = DRAFT (by default)
- viewsCount = 0
- createdAt, updatedAt = now

Service Method:
  BikeListingResponse createListing(Integer sellerId, CreateListingRequest req)

Validation Rules:
- ✅ title: required, max 255 chars
- ✅ price: required, >= 0
- ✅ bikeType: required, max 50 chars
- ✅ brand: required, max 100 chars
- ✅ model: required, max 100 chars
- ❌ other fields: optional

Error Cases:
- 400 BAD_REQUEST: Validation failed
- 404 NOT_FOUND: Seller not found
- 500 INTERNAL_SERVER_ERROR: Database error

Example Request:
{
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "manufactureYear": 2023,
  "condition": "Good",
  "usageTime": "2 years",
  "reasonForSale": "Muốn nâng cấp",
  "price": 45000000,
  "locationCity": "Ho Chi Minh",
  "pickupAddress": "123 Tran Hung Dao St",
  "saveDraft": true
}

Example Response (201 CREATED):
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:00:00",
  "updatedAt": "2026-01-31T19:00:00"
}
```

#### Endpoint 2: `POST /api/seller/listings/{id}/submit` - Submit for Approval
```
Method: POST
Path: /api/seller/listings/{listing_id}/submit
Status Code: 200 OK

Request DTO: SubmitListingRequest
- sellerId: Integer @NotNull (required)
- listingId: Integer @NotNull (required)

Response DTO: BikeListingResponse
- Same listing with status = PENDING

Service Method:
  BikeListingResponse submitListing(Integer sellerId, Integer listingId, SubmitListingRequest req)

Validation Rules:
- Listing must exist and belong to seller
- Listing status must be DRAFT
- Required fields must be present: title, price, bikeType, brand, model
- Transition: DRAFT → PENDING

Error Cases:
- 404 NOT_FOUND: Seller or listing not found
- 400 BAD_REQUEST: 
  - Listing is not DRAFT status
  - Required fields are missing
  - Seller doesn't own the listing

Example Request:
{
  "sellerId": 1,
  "listingId": 10
}

Example Response (200 OK):
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "status": "PENDING",  // ← Changed from DRAFT
  "updatedAt": "2026-01-31T19:05:00"
}
```

---

### **S-14: Preview Listing**

#### Endpoint 3: `GET /api/seller/listings/{id}/preview` - Preview Listing Data
```
Method: GET
Path: /api/seller/listings/{listing_id}/preview
Status Code: 200 OK

Request: GET params
- listing_id: Integer (path param)
- sellerId: Integer (from header or body) ← Need to add to request

Request DTO: PreviewListingRequest
- sellerId: Integer @NotNull (required)
- listingId: Integer @NotNull (required)

Response DTO: PreviewListingResponse (complete listing data)
- listingId
- sellerId
- title
- description
- bikeType
- brand
- model
- manufactureYear
- condition
- usageTime
- reasonForSale
- price
- locationCity
- pickupAddress
- status
- viewsCount
- createdAt
- updatedAt
- sellerInfo (optional): name, phone, rating

Service Method:
  PreviewListingResponse previewListing(Integer sellerId, Integer listingId)

Validation Rules:
- Listing must exist and belong to seller

Error Cases:
- 404 NOT_FOUND: Seller or listing not found

Example Request:
POST /api/seller/listings/preview
{
  "sellerId": 1,
  "listingId": 10
}

Example Response (200 OK):
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "manufactureYear": 2023,
  "condition": "Good",
  "usageTime": "2 years",
  "reasonForSale": "Muốn nâng cấp",
  "price": 45000000,
  "locationCity": "Ho Chi Minh",
  "pickupAddress": "123 Tran Hung Dao St",
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:00:00",
  "updatedAt": "2026-01-31T19:00:00"
}
```

---

### **S-18: Draft Management**

#### Endpoint 4: `GET /api/seller/drafts` - List Draft Listings
```
Method: GET
Path: /api/seller/drafts
Status Code: 200 OK

Request DTO: GetDraftsRequest
- sellerId: Integer @NotNull (required)
- sort: String (optional) - "newest"/"oldest", default "newest"
- page: Integer @Min(0) (optional) - default 0
- pageSize: Integer @Min(1) (optional) - default 10

Response DTO: Page<SellerListingResponse>
- Paginated list of DRAFT listings

Service Method:
  Page<SellerListingResponse> getDraftListings(Integer sellerId, String sort, int page, int pageSize)

Validation Rules:
- Filter by seller AND status = DRAFT

Error Cases:
- 404 NOT_FOUND: Seller not found
- 400 BAD_REQUEST: Invalid page/pageSize

Example Request:
POST /api/seller/drafts
{
  "sellerId": 1,
  "page": 0,
  "pageSize": 10
}

Example Response (200 OK):
{
  "content": [
    {
      "listingId": 10,
      "title": "Honda CB150R 2023",
      "brand": "Honda",
      "price": 45000000,
      "status": "DRAFT",
      "createdAt": "2026-01-31T19:00:00"
    },
    ...
  ],
  "totalElements": 3,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

#### Endpoint 5: `DELETE /api/seller/drafts/{id}` - Delete Draft
```
Method: DELETE
Path: /api/seller/drafts/{listing_id}
Status Code: 204 NO_CONTENT

Request DTO: DeleteDraftRequest
- sellerId: Integer @NotNull (required)
- listingId: Integer @NotNull (required)

Response: Empty (204 NO_CONTENT)

Service Method:
  void deleteDraft(Integer sellerId, Integer listingId)

Validation Rules:
- Listing must exist and belong to seller
- Listing status must be DRAFT
- Hard delete from database

Error Cases:
- 404 NOT_FOUND: Seller or listing not found
- 400 BAD_REQUEST: Listing is not DRAFT status

Example Request:
POST /api/seller/drafts/delete
{
  "sellerId": 1,
  "listingId": 10
}

Example Response (204 NO_CONTENT):
(no body)
```

#### Endpoint 6: `POST /api/seller/drafts/{id}/submit` - Submit Draft for Approval
```
Method: POST
Path: /api/seller/drafts/{listing_id}/submit
Status Code: 200 OK

Request DTO: SubmitDraftRequest
- sellerId: Integer @NotNull (required)
- listingId: Integer @NotNull (required)

Response DTO: BikeListingResponse
- Listing with status = PENDING

Service Method:
  BikeListingResponse submitDraft(Integer sellerId, Integer listingId, SubmitDraftRequest req)
  
  OR reuse: submitListing(Integer sellerId, Integer listingId)

Validation Rules:
- Listing must exist and belong to seller
- Listing status must be DRAFT
- Required fields present: title, price, bikeType, brand, model
- Transition: DRAFT → PENDING

Error Cases:
- 404 NOT_FOUND: Seller or listing not found
- 400 BAD_REQUEST:
  - Listing is not DRAFT
  - Required fields missing

Example Request:
POST /api/seller/drafts/10/submit
{
  "sellerId": 1,
  "listingId": 10
}

Example Response (200 OK):
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "status": "PENDING",  // ← Changed from DRAFT
  "updatedAt": "2026-01-31T19:05:00"
}
```

---

## 📋 New DTOs Required

### 1. CreateListingRequest
```java
public class CreateListingRequest {
    @NotNull
    public Integer sellerId;
    
    @NotBlank
    @Size(max = 255)
    public String title;  // REQUIRED
    
    public String description;
    
    @NotBlank
    @Size(max = 50)
    public String bikeType;  // REQUIRED
    
    @NotBlank
    @Size(max = 100)
    public String brand;  // REQUIRED
    
    @NotBlank
    @Size(max = 100)
    public String model;  // REQUIRED
    
    public Integer manufactureYear;
    
    @Size(max = 50)
    public String condition;
    
    @Size(max = 100)
    public String usageTime;
    
    public String reasonForSale;
    
    @NotNull
    @PositiveOrZero
    public BigDecimal price;  // REQUIRED
    
    @Size(max = 100)
    public String locationCity;
    
    public String pickupAddress;
    
    public Boolean saveDraft = true;  // true = DRAFT, false = direct submit
}
```

### 2. SubmitListingRequest
```java
public class SubmitListingRequest {
    @NotNull
    public Integer sellerId;
    
    @NotNull
    public Integer listingId;
}
```

### 3. PreviewListingResponse
```java
public class PreviewListingResponse {
    public Integer listingId;
    public Integer sellerId;
    public String title;
    public String description;
    public String bikeType;
    public String brand;
    public String model;
    public Integer manufactureYear;
    public String condition;
    public String usageTime;
    public String reasonForSale;
    public BigDecimal price;
    public String locationCity;
    public String pickupAddress;
    public BikeListingStatus status;
    public Integer viewsCount;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    
    public static PreviewListingResponse from(BikeListing b) { ... }
}
```

### 4. GetDraftsRequest
```java
public class GetDraftsRequest {
    @NotNull
    public Integer sellerId;
    
    public String sort = "newest";
    
    @Min(0)
    public Integer page = 0;
    
    @Min(1)
    public Integer pageSize = 10;
}
```

### 5. DeleteDraftRequest
```java
public class DeleteDraftRequest {
    @NotNull
    public Integer sellerId;
    
    @NotNull
    public Integer listingId;
}
```

### 6. SubmitDraftRequest
```java
public class SubmitDraftRequest {
    @NotNull
    public Integer sellerId;
    
    @NotNull
    public Integer listingId;
}
```

---

## 🔧 BikeListingStatus Enum Update

### Current:
```java
public enum BikeListingStatus {
    APPROVED,
    REJECTED,
    PENDING
}
```

### Updated:
```java
public enum BikeListingStatus {
    DRAFT,      // ✅ NEW - listing created but not submitted
    PENDING,    // listing submitted, waiting approval
    APPROVED,   // admin approved
    REJECTED    // admin rejected
}
```

---

## 📝 SellerService Methods to Add/Update

```java
// S-12: Create Listing
public BikeListingResponse createListing(Integer sellerId, CreateListingRequest req)
  - Validate seller exists
  - Validate required fields: title, price, bikeType, brand, model
  - Create BikeListing with status = DRAFT (or PENDING if saveDraft=false)
  - Return BikeListingResponse

// S-12: Submit Listing
public BikeListingResponse submitListing(Integer sellerId, Integer listingId)
  - Validate seller exists
  - Fetch listing by listingId + seller ownership
  - Validate listing status = DRAFT
  - Validate required fields present
  - Update status: DRAFT → PENDING
  - Save and return BikeListingResponse

// S-14: Preview
public PreviewListingResponse previewListing(Integer sellerId, Integer listingId)
  - Validate seller exists
  - Fetch listing by listingId + seller ownership
  - Return PreviewListingResponse

// S-18: Get Drafts
public Page<SellerListingResponse> getDraftListings(Integer sellerId, String sort, int page, int pageSize)
  - Validate seller exists
  - Query listings where seller=sellerId AND status=DRAFT
  - Apply sort (newest/oldest)
  - Return paginated results

// S-18: Delete Draft
public void deleteDraft(Integer sellerId, Integer listingId)
  - Validate seller exists
  - Fetch listing by listingId + seller ownership
  - Validate status = DRAFT
  - Hard delete from database

// S-18: Submit Draft
public BikeListingResponse submitDraft(Integer sellerId, Integer listingId)
  - Same as submitListing() - reuse method or alias
```

---

## ✅ Validation Rules Summary

| Field | Create | Submit | Delete | Preview |
|-------|--------|--------|--------|---------|
| title | ✅ Required | - | - | - |
| bikeType | ✅ Required | - | - | - |
| brand | ✅ Required | - | - | - |
| model | ✅ Required | - | - | - |
| price | ✅ Required | - | - | - |
| description | Optional | - | - | - |
| Others | Optional | - | - | - |
| Status | Must be DRAFT | Must be DRAFT | Must be DRAFT | - |

---

## 🚨 Error Scenarios

| Scenario | HTTP Status | Message |
|----------|-------------|---------|
| Seller not found | 404 NOT_FOUND | "Seller not found" |
| Listing not found | 404 NOT_FOUND | "Listing not found" |
| Seller doesn't own listing | 400 BAD_REQUEST | "Listing not owned by seller" |
| Required field missing on create | 400 BAD_REQUEST | "Title is required" |
| Required field missing on submit | 400 BAD_REQUEST | "Cannot submit: missing required fields" |
| Submit non-DRAFT listing | 400 BAD_REQUEST | "Only DRAFT listings can be submitted" |
| Delete non-DRAFT listing | 400 BAD_REQUEST | "Only DRAFT listings can be deleted" |
| Invalid page/pageSize | 400 BAD_REQUEST | "Page must be >= 0" |
| Validation error | 400 BAD_REQUEST | Field validation messages |

---

## 🔐 Security Considerations

- ✅ Extract sellerId from JWT token (authentication layer)
- ✅ Validate seller owns the listing (service layer)
- ✅ Only sellers can create/modify their listings
- ✅ Drafts are private to owner (not visible to others)

---

## 📊 Database Impact

### New Indexes Recommended:
```sql
CREATE INDEX idx_bike_listings_seller_status ON bike_listings(seller_id, status);
CREATE INDEX idx_bike_listings_status ON bike_listings(status);
```

### BikeListing Entity Updates:
- No schema changes (DRAFT added to existing enum)
- All fields already exist in BikeListing entity

---

## 🧪 Test Cases to Cover

### Positive Cases:
- ✅ Create listing → status DRAFT
- ✅ Create + submit → status PENDING
- ✅ Preview draft listing
- ✅ List drafts with pagination
- ✅ Delete draft
- ✅ Submit draft for approval

### Negative Cases:
- ❌ Create without required fields
- ❌ Submit non-existent listing
- ❌ Submit non-DRAFT listing
- ❌ Delete non-DRAFT listing
- ❌ Seller trying to access other seller's listing
- ❌ Invalid pagination params

---

## 📝 Estimated Implementation Time

- BikeListingStatus enum update: 5 min
- Create DTOs: 20 min
- SellerService methods: 30 min
- SellerController endpoints: 20 min
- Testing: 30 min
- **Total: ~2 hours**

---

## ⏭️ Implementation Sequence

1. Update BikeListingStatus enum
2. Create all DTOs
3. Implement SellerService methods
4. Update SellerController endpoints
5. Compile & test
6. Update SecurityConfig if needed

---
