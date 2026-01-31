# CycleX-BE Seller API Documentation

**Version:** 1.0  
**Date:** January 31, 2026  
**Status:** Batch 1 & Batch 2 Complete  

---

## 📋 Table of Contents

1. [Batch 1: Dashboard & Listings (Read-Only)](#batch-1)
2. [Batch 2: Create & Submit Listings](#batch-2)
3. [Error Response Format](#error-responses)
4. [Status Codes Reference](#status-codes)

---

## 🔐 Authentication

All endpoints require:
- **Header:** `Authorization: Bearer <JWT_TOKEN>` (future implementation)
- **Body:** `sellerId` field in request body (current implementation)

---

<a id="batch-1"></a>

# 📌 Batch 1: Seller Dashboard & My Listings

## 1️⃣ Get Dashboard Statistics

**Detail:** Retrieve seller's dashboard statistics with counts of listings by status

**URL:** `/api/seller/dashboard/stats`

**Method:** `GET`

**Body:**
```json
{
  "sellerId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "approvedCount": 5,
  "pendingCount": 2,
  "rejectedCount": 1,
  "totalListings": 8,
  "totalViews": 245
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Invalid JSON | 400 BAD_REQUEST | `{"error": "JSON parse error"}` |

---

## 2️⃣ Search & Filter My Listings

**Detail:** Get seller's listings with advanced filtering by status, title, brand, model, price range

**URL:** `/api/seller/listings/search`

**Method:** `POST`

**Body:**
```json
{
  "sellerId": 1,
  "status": "APPROVED",
  "title": "Honda",
  "brand": "Honda",
  "model": "CB150R",
  "minPrice": 30000000,
  "maxPrice": 50000000,
  "sort": "newest",
  "page": 0,
  "pageSize": 10
}
```

**Success Response:** `200 OK`
```json
{
  "content": [
    {
      "listingId": 1,
      "title": "Honda CB150R 2023",
      "brand": "Honda",
      "model": "CB150R",
      "price": 45000000,
      "status": "APPROVED",
      "viewsCount": 120,
      "createdAt": "2026-01-20T10:30:00",
      "updatedAt": "2026-01-25T15:45:00"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "number": 0,
  "size": 10,
  "first": true,
  "last": true,
  "empty": false
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Invalid status | 400 BAD_REQUEST | `{"error": "Invalid status: INVALID_STATUS"}` |
| Invalid page | 400 BAD_REQUEST | `{"error": "page: Page must be >= 0"}` |
| Invalid pageSize | 400 BAD_REQUEST | `{"error": "pageSize: Page size must be >= 1"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |

---

## 3️⃣ Get Listing Detail

**Detail:** Get complete details of a specific listing

**URL:** `/api/seller/listings/detail`

**Method:** `POST`

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
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
  "status": "APPROVED",
  "viewsCount": 120,
  "createdAt": "2026-01-20T10:30:00",
  "updatedAt": "2026-01-25T15:45:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

## 4️⃣ Get Rejection Reason

**Detail:** Get rejection reason for a rejected listing

**URL:** `/api/seller/listings/rejection`

**Method:** `POST`

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 3
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 3,
  "sellerId": 1,
  "title": "Test Bike",
  "brand": "Test",
  "model": "Test",
  "price": 30000000,
  "status": "REJECTED",
  "viewsCount": 0,
  "createdAt": "2026-01-15T09:00:00",
  "updatedAt": "2026-01-20T14:30:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Listing not rejected | 400 BAD_REQUEST | `{"error": "Listing is not rejected"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="batch-2"></a>

# 📌 Batch 2: Create & Submit Listings

## 5️⃣ Create New Listing

**Detail:** Create a new listing (defaults to DRAFT status)

**URL:** `/api/seller/listings`

**Method:** `POST`

**Body:**
```json
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
```

**Success Response:** `201 CREATED`
```json
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "manufacturYear": 2023,
  "condition": "Good",
  "usageTime": "2 years",
  "reasonForSale": "Muốn nâng cấp",
  "price": 45000000,
  "locationCity": "Ho Chi Minh",
  "pickupAddress": "123 Tran Hung Dao St",
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:30:00",
  "updatedAt": "2026-01-31T19:30:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Missing required field (title) | 400 BAD_REQUEST | `{"error": "title: Title is required"}` |
| Missing required field (price) | 400 BAD_REQUEST | `{"error": "price: Price is required"}` |
| Missing required field (bikeType) | 400 BAD_REQUEST | `{"error": "bikeType: Bike type is required"}` |
| Missing required field (brand) | 400 BAD_REQUEST | `{"error": "brand: Brand is required"}` |
| Missing required field (model) | 400 BAD_REQUEST | `{"error": "model: Model is required"}` |
| Title too long | 400 BAD_REQUEST | `{"error": "title: Title must be <= 255 characters"}` |
| Price negative | 400 BAD_REQUEST | `{"error": "price: Price must be >= 0"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |

---

## 6️⃣ Submit Listing for Approval

**Detail:** Submit a DRAFT listing for admin approval (DRAFT → PENDING)

**URL:** `/api/seller/listings/{listing_id}/submit`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of listing to submit)

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 10
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "PENDING",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:30:00",
  "updatedAt": "2026-01-31T19:35:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Listing not DRAFT | 400 BAD_REQUEST | `{"error": "Only DRAFT listings can be submitted"}` |
| Required field missing (title) | 400 BAD_REQUEST | `{"error": "Cannot submit: title is required"}` |
| Required field missing (price) | 400 BAD_REQUEST | `{"error": "Cannot submit: price is required"}` |
| Required field missing (bikeType) | 400 BAD_REQUEST | `{"error": "Cannot submit: bike type is required"}` |
| Required field missing (brand) | 400 BAD_REQUEST | `{"error": "Cannot submit: brand is required"}` |
| Required field missing (model) | 400 BAD_REQUEST | `{"error": "Cannot submit: model is required"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

## 7️⃣ Preview Listing

**Detail:** Preview complete listing data before submitting

**URL:** `/api/seller/listings/preview`

**Method:** `POST`

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 10
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "manufacturYear": 2023,
  "condition": "Good",
  "usageTime": "2 years",
  "reasonForSale": "Muốn nâng cấp",
  "price": 45000000,
  "locationCity": "Ho Chi Minh",
  "pickupAddress": "123 Tran Hung Dao St",
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:30:00",
  "updatedAt": "2026-01-31T19:30:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

## 8️⃣ Get All Drafts

**Detail:** List all draft listings with pagination and sorting

**URL:** `/api/seller/drafts`

**Method:** `POST`

**Body:**
```json
{
  "sellerId": 1,
  "sort": "newest",
  "page": 0,
  "pageSize": 10
}
```

**Success Response:** `200 OK`
```json
{
  "content": [
    {
      "listingId": 10,
      "title": "Honda CB150R 2023",
      "brand": "Honda",
      "model": "CB150R",
      "price": 45000000,
      "status": "DRAFT",
      "viewsCount": 0,
      "createdAt": "2026-01-31T19:30:00",
      "updatedAt": "2026-01-31T19:30:00"
    },
    {
      "listingId": 11,
      "title": "Yamaha YZF-R15",
      "brand": "Yamaha",
      "model": "YZF-R15",
      "price": 50000000,
      "status": "DRAFT",
      "viewsCount": 0,
      "createdAt": "2026-01-30T14:20:00",
      "updatedAt": "2026-01-30T14:20:00"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0,
  "size": 10,
  "first": true,
  "last": true,
  "empty": false
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Invalid page | 400 BAD_REQUEST | `{"error": "page: Page must be >= 0"}` |
| Invalid pageSize | 400 BAD_REQUEST | `{"error": "pageSize: Page size must be >= 1"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |

---

## 9️⃣ Delete Draft

**Detail:** Delete a draft listing (only DRAFT listings can be deleted)

**URL:** `/api/seller/drafts/{listing_id}`

**Method:** `DELETE`

**Path Parameters:**
- `listing_id`: Integer (ID of draft to delete)

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 10
}
```

**Success Response:** `204 NO_CONTENT`
```
(No body returned)
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Listing not DRAFT | 400 BAD_REQUEST | `{"error": "Only DRAFT listings can be deleted"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

## 🔟 Submit Draft for Approval

**Detail:** Submit a DRAFT listing from the drafts endpoint (same as endpoint #6, but from drafts list)

**URL:** `/api/seller/drafts/{listing_id}/submit`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of draft to submit)

**Body:**
```json
{
  "sellerId": 1,
  "listingId": 10
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "description": "Xe máy cũ, còn tốt",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "PENDING",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:30:00",
  "updatedAt": "2026-01-31T19:35:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Seller not found | 404 NOT_FOUND | `{"error": "Seller not found"}` |
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found or not owned by seller"}` |
| Listing not DRAFT | 400 BAD_REQUEST | `{"error": "Only DRAFT listings can be submitted"}` |
| Required field missing | 400 BAD_REQUEST | `{"error": "Cannot submit: [field] is required"}` |
| Missing sellerId | 400 BAD_REQUEST | `{"error": "sellerId: Seller ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="error-responses"></a>

# ⚠️ Error Response Format

### Standard Error Response

```json
{
  "error": "Error message describing what went wrong"
}
```

### Validation Error Response

```json
{
  "error": "Validation failed",
  "errors": [
    "field1: error message",
    "field2: error message"
  ]
}
```

---

<a id="status-codes"></a>

# 📊 HTTP Status Codes Reference

| Code | Meaning | Common Cause |
|------|---------|--------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Delete successful (no body) |
| 400 | Bad Request | Validation error, invalid input |
| 404 | Not Found | Seller/Listing not found |
| 500 | Internal Error | Server error |

---

## 🧪 Quick Test Curl Commands

### Create Draft Listing
```bash
curl -X POST http://localhost:8080/api/seller/listings \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "title": "Test Bike",
    "bikeType": "Motorcycle",
    "brand": "Honda",
    "model": "CB150R",
    "price": 45000000,
    "saveDraft": true
  }'
```

### Submit for Approval
```bash
curl -X POST http://localhost:8080/api/seller/listings/10/submit \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "listingId": 10
  }'
```

### Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/seller/dashboard/stats \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1
  }'
```

### List Drafts
```bash
curl -X POST http://localhost:8080/api/seller/drafts \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "page": 0,
    "pageSize": 10
  }'
```

### Preview Listing
```bash
curl -X POST http://localhost:8080/api/seller/listings/preview \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "listingId": 10
  }'
```

### Delete Draft
```bash
curl -X DELETE http://localhost:8080/api/seller/drafts/10 \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "listingId": 10
  }'
```

### Search Listings
```bash
curl -X POST http://localhost:8080/api/seller/listings/search \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "status": "APPROVED",
    "title": "Honda",
    "brand": "Honda",
    "page": 0,
    "pageSize": 10
  }'
```

---

## 🔄 Listing Status Lifecycle

```
CREATE LISTING
  ↓
saveDraft: true  →  DRAFT (can edit, preview, delete)
saveDraft: false →  PENDING (direct submission)
  ↓
SUBMIT / SUBMIT DRAFT
  ↓
PENDING (waiting admin approval)
  ↓
Admin Review
  ↓
APPROVED (published) or REJECTED (back to private)
```

---

## ✅ Testing Checklist

### Batch 1 Endpoints
- [ ] Dashboard Stats (single seller, multiple counts)
- [ ] Search with all filter combinations
- [ ] Listing Detail (authorized access)
- [ ] Rejection Reason (for REJECTED listings only)

### Batch 2 Endpoints
- [ ] Create Draft (required fields validation)
- [ ] Create with auto-submit (saveDraft=false)
- [ ] Submit DRAFT to PENDING
- [ ] Preview listing
- [ ] List drafts with pagination
- [ ] Delete draft (DRAFT only)
- [ ] Submit from drafts endpoint

### Error Cases
- [ ] Seller not found
- [ ] Listing not found
- [ ] Validation errors
- [ ] Status transition errors
- [ ] Ownership validation

---

**End of API Documentation**

*Last Updated: 2026-01-31*  
*Batch 1 + Batch 2 Complete*
