# Inspector API Documentation

**Version:** 1.0  
**Date:** January 31, 2026  
**Status:** Complete  

---

## 📋 Table of Contents

1. [Dashboard Stats](#endpoint-1-dashboard-stats)
2. [List Listings for Review](#endpoint-2-list-listings)
3. [Get Listing Detail](#endpoint-3-listing-detail)
4. [Lock Listing](#endpoint-4-lock-listing)
5. [Unlock Listing](#endpoint-5-unlock-listing)
6. [Approve Listing](#endpoint-6-approve-listing)
7. [Reject Listing](#endpoint-7-reject-listing)
8. [Review History](#endpoint-8-review-history)
9. [Review Detail](#endpoint-9-review-detail)
10. [List Disputes](#endpoint-10-disputes)
11. [Dispute Detail](#endpoint-11-dispute-detail)

---

<a id="endpoint-1-dashboard-stats"></a>

## 1️⃣ Inspector Dashboard Stats

**Detail:** Get dashboard statistics with listing and dispute counts

**URL:** `/api/inspector/dashboard/stats`

**Method:** `GET`

**Body:**
```json
{
  "inspectorId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "pendingCount": 15,
  "reviewingCount": 3,
  "approvedCount": 45,
  "rejectedCount": 5,
  "disputeCount": 2
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Inspector not found | 404 NOT_FOUND | `{"error": "Inspector not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |

---

<a id="endpoint-2-list-listings"></a>

## 2️⃣ List Listings for Review

**Detail:** Get listings pending or under review with filtering and pagination

**URL:** `/api/inspector/listings`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "status": "ALL",
  "sort": "newest",
  "page": 0,
  "pageSize": 10
}
```

**Parameters:**
- `status`: `ALL` | `PENDING` | `REVIEWING` (optional)
- `sort`: `newest` (DESC) or `oldest` (ASC), default: `newest`
- `page`: 0-indexed, default: 0
- `pageSize`: default: 10

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
      "status": "PENDING",
      "viewsCount": 0,
      "createdAt": "2026-01-31T10:00:00",
      "updatedAt": "2026-01-31T10:00:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "number": 0,
  "size": 10
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Invalid page | 400 BAD_REQUEST | `{"error": "page: Page must be >= 0"}` |
| Invalid pageSize | 400 BAD_REQUEST | `{"error": "pageSize: Page size must be >= 1"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |

---

<a id="endpoint-3-listing-detail"></a>

## 3️⃣ Get Listing Detail for Review

**Detail:** Get complete listing details for review (includes all bike information)

**URL:** `/api/inspector/listings/detail`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "listingId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
  "sellerId": 5,
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
  "status": "PENDING",
  "viewsCount": 0,
  "createdAt": "2026-01-31T10:00:00",
  "updatedAt": "2026-01-31T10:00:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="endpoint-4-lock-listing"></a>

## 4️⃣ Lock Listing for Review

**Detail:** Lock listing to REVIEWING status (prevents seller from editing)

**URL:** `/api/inspector/listings/{listing_id}/lock`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of listing to lock)

**Body:**
```json
{
  "inspectorId": 1,
  "listingId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
  "sellerId": 5,
  "title": "Honda CB150R 2023",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "REVIEWING",
  "viewsCount": 0,
  "createdAt": "2026-01-31T10:00:00",
  "updatedAt": "2026-01-31T10:15:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found"}` |
| Not PENDING status | 400 BAD_REQUEST | `{"error": "Only PENDING listings can be locked"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="endpoint-5-unlock-listing"></a>

## 5️⃣ Unlock Listing from Review

**Detail:** Unlock listing and revert to PENDING status (if no decision made yet)

**URL:** `/api/inspector/listings/{listing_id}/unlock`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of listing to unlock)

**Body:**
```json
{
  "inspectorId": 1,
  "listingId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
  "title": "Honda CB150R 2023",
  "brand": "Honda",
  "price": 45000000,
  "status": "PENDING",
  "updatedAt": "2026-01-31T10:20:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="endpoint-6-approve-listing"></a>

## 6️⃣ Approve Listing

**Detail:** Approve listing and change status to APPROVED (makes it public)

**URL:** `/api/inspector/listings/{listing_id}/approve`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of listing to approve)

**Body:**
```json
{
  "inspectorId": 1,
  "listingId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
  "title": "Honda CB150R 2023",
  "brand": "Honda",
  "price": 45000000,
  "status": "APPROVED",
  "updatedAt": "2026-01-31T10:25:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |

---

<a id="endpoint-7-reject-listing"></a>

## 7️⃣ Reject Listing

**Detail:** Reject listing with reason code and detailed explanation

**URL:** `/api/inspector/listings/{listing_id}/reject`

**Method:** `POST`

**Path Parameters:**
- `listing_id`: Integer (ID of listing to reject)

**Body:**
```json
{
  "inspectorId": 1,
  "listingId": 1,
  "reasonCode": "DUPLICATE",
  "reasonText": "This bike listing is a duplicate of listing #5",
  "note": "Similar bike model and location"
}
```

**Reason Codes:**
- `DUPLICATE` - Duplicate listing
- `INVALID_INFO` - Invalid or misleading information
- `LOW_QUALITY` - Low quality photos/description
- `INAPPROPRIATE` - Inappropriate content
- `OTHER` - Other reason

**Success Response:** `200 OK`
```json
{
  "listingId": 1,
  "title": "Honda CB150R 2023",
  "brand": "Honda",
  "price": 45000000,
  "status": "REJECTED",
  "updatedAt": "2026-01-31T10:30:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Listing not found | 404 NOT_FOUND | `{"error": "Listing not found"}` |
| Not PENDING status | 400 BAD_REQUEST | `{"error": "Only PENDING listings can be rejected"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing listingId | 400 BAD_REQUEST | `{"error": "listingId: Listing ID is required"}` |
| Missing reasonCode | 400 BAD_REQUEST | `{"error": "reasonCode: Reason code is required"}` |
| Missing reasonText | 400 BAD_REQUEST | `{"error": "reasonText: Reason text is required"}` |

---

<a id="endpoint-8-review-history"></a>

## 8️⃣ Review History

**Detail:** Get inspector's review history with optional date range filter

**URL:** `/api/inspector/reviews`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "from": "2026-01-01",
  "to": "2026-01-31",
  "page": 0,
  "pageSize": 10
}
```

**Parameters:**
- `from`: YYYY-MM-DD (optional)
- `to`: YYYY-MM-DD (optional)
- `page`: 0-indexed, default: 0
- `pageSize`: default: 10

**Success Response:** `200 OK`
```json
{
  "content": [
    {
      "reviewId": 1,
      "listingId": 5,
      "inspectorId": 1,
      "decision": "APPROVED",
      "reasonCode": null,
      "reviewedAt": "2026-01-31T10:25:00"
    },
    {
      "reviewId": 2,
      "listingId": 6,
      "inspectorId": 1,
      "decision": "REJECTED",
      "reasonCode": "DUPLICATE",
      "reviewedAt": "2026-01-31T10:30:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "number": 0,
  "size": 10
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Inspector not found | 404 NOT_FOUND | `{"error": "Inspector not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Invalid page | 400 BAD_REQUEST | `{"error": "page: Page must be >= 0"}` |
| Invalid pageSize | 400 BAD_REQUEST | `{"error": "pageSize: Page size must be >= 1"}` |

---

<a id="endpoint-9-review-detail"></a>

## 9️⃣ Review Detail

**Detail:** Get detailed information about a specific review

**URL:** `/api/inspector/reviews/detail`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "reviewId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "reviewId": 1,
  "listingId": 5,
  "inspectorId": 1,
  "decision": "APPROVED",
  "reasonCode": null,
  "reasonText": null,
  "note": null,
  "reviewedAt": "2026-01-31T10:25:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Review not found | 404 NOT_FOUND | `{"error": "Review not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing reviewId | 400 BAD_REQUEST | `{"error": "reviewId: Review ID is required"}` |

---

<a id="endpoint-10-disputes"></a>

## 🔟 List Disputes

**Detail:** Get list of disputes for routing and statistics

**URL:** `/api/inspector/disputes`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "status": "OPEN",
  "page": 0,
  "pageSize": 10
}
```

**Parameters:**
- `status`: `OPEN` | `RESOLVED` (optional)
- `page`: 0-indexed, default: 0
- `pageSize`: default: 10

**Success Response:** `200 OK`
```json
{
  "content": [
    {
      "disputeId": 1,
      "listingId": 10,
      "buyerId": 3,
      "sellerId": 5,
      "subject": "Item not matching description",
      "status": "OPEN",
      "createdAt": "2026-01-30T14:00:00"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Invalid page | 400 BAD_REQUEST | `{"error": "page: Page must be >= 0"}` |
| Invalid pageSize | 400 BAD_REQUEST | `{"error": "pageSize: Page size must be >= 1"}` |

---

<a id="endpoint-11-dispute-detail"></a>

## 1️⃣1️⃣ Dispute Detail

**Detail:** Get detailed information about a specific dispute

**URL:** `/api/inspector/disputes/detail`

**Method:** `POST`

**Body:**
```json
{
  "inspectorId": 1,
  "disputeId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "disputeId": 1,
  "listingId": 10,
  "buyerId": 3,
  "sellerId": 5,
  "subject": "Item not matching description",
  "description": "The bike I received does not match the description in the listing",
  "status": "OPEN",
  "createdAt": "2026-01-30T14:00:00",
  "updatedAt": "2026-01-30T14:00:00"
}
```

**Fail Response:**

| Reason | Status | Response |
|--------|--------|----------|
| Dispute not found | 404 NOT_FOUND | `{"error": "Dispute not found"}` |
| Missing inspectorId | 400 BAD_REQUEST | `{"error": "inspectorId: Inspector ID is required"}` |
| Missing disputeId | 400 BAD_REQUEST | `{"error": "disputeId: Dispute ID is required"}` |

---

## 🧪 Curl Examples

### Get Dashboard Stats
```bash
curl -X GET http://localhost:8080/api/inspector/dashboard/stats \
  -H "Content-Type: application/json" \
  -d '{
    "inspectorId": 1
  }'
```

### List Listings for Review
```bash
curl -X POST http://localhost:8080/api/inspector/listings \
  -H "Content-Type: application/json" \
  -d '{
    "inspectorId": 1,
    "status": "PENDING",
    "sort": "newest",
    "page": 0,
    "pageSize": 10
  }'
```

### Lock Listing
```bash
curl -X POST http://localhost:8080/api/inspector/listings/1/lock \
  -H "Content-Type: application/json" \
  -d '{
    "inspectorId": 1,
    "listingId": 1
  }'
```

### Approve Listing
```bash
curl -X POST http://localhost:8080/api/inspector/listings/1/approve \
  -H "Content-Type: application/json" \
  -d '{
    "inspectorId": 1,
    "listingId": 1
  }'
```

### Reject Listing
```bash
curl -X POST http://localhost:8080/api/inspector/listings/1/reject \
  -H "Content-Type: application/json" \
  -d '{
    "inspectorId": 1,
    "listingId": 1,
    "reasonCode": "DUPLICATE",
    "reasonText": "This is a duplicate listing",
    "note": "Already approved listing ID #5"
  }'
```

---

## 🔄 Status Flow

```
PENDING (submitted by seller)
  ↓ (inspector locks)
REVIEWING (inspector reviewing)
  ↓
├─ APPROVE → APPROVED (published to buyers)
├─ REJECT → REJECTED (back to private, seller sees reason)
└─ UNLOCK → PENDING (reverted if unsure)
```

---

## ✅ Complete Implementation

All 11 endpoints fully implemented with:
- ✅ Request validation via DTOs
- ✅ @Valid annotation
- ✅ Proper error handling
- ✅ Service integration
- ✅ Clear documentation

---

**Inspector API - Ready for Testing** 🚀

*Date: 2026-01-31*
