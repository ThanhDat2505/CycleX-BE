# InspectorController - Endpoints Overview

**Date:** January 31, 2026  
**Status:** Controller Skeleton Created - Ready for Service Implementation  

---

## 📋 Overview

`InspectorController` quản lý toàn bộ workflow duyệt listing và xử lý tranh chấp.

---

## 🎯 Endpoints List (10 Endpoints)

### S-20: Dashboard Thống Kê

**1. Inspector Dashboard Stats**
```
Endpoint: GET /api/inspector/dashboard/stats
Method:   GET
Purpose:  Get dashboard statistics with PENDING and DISPUTE counts
Response: {
  "pendingCount": 15,
  "reviewingCount": 3,
  "approvedCount": 45,
  "rejectedCount": 5,
  "disputeCount": 2
}
```

---

### S-21: Danh Sách Listing Pending/Reviewing

**2. Get Listings for Review**
```
Endpoint: POST /api/inspector/listings
Method:   POST
Purpose:  List listings with filter by status and pagination
Query:    
  - status: ALL|PENDING|REVIEWING (filter)
  - sort: newest|oldest (sort by createdAt)
  - page: 0 (0-indexed)
  - page_size: 10
Response: Page<ListingForReview>
  {
    "content": [...],
    "totalElements": 25,
    "totalPages": 3,
    ...
  }
```

---

### S-22/S-23: Chi Tiết Duyệt

**3. Get Listing Detail for Review**
```
Endpoint: POST /api/inspector/listings/detail
Method:   POST
Purpose:  Get listing detail for review (includes images)
Body:     {
  "listingId": 1
}
Response: {
  "listingId": 1,
  "sellerId": 5,
  "title": "Honda CB150R",
  "description": "...",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "PENDING|REVIEWING",
  "createdAt": "2026-01-31T...",
  "updatedAt": "2026-01-31T...",
  "images": [...]
}
```

**4. Lock Listing for Review**
```
Endpoint: POST /api/inspector/listings/{listing_id}/lock
Method:   POST
Purpose:  Lock listing to REVIEWING status and prevent seller editing
Path:     listing_id (Integer)
Effect:   
  - Status: PENDING → REVIEWING
  - Lock owner (prevent seller modification)
  - Record locked timestamp
Response: 200 OK with updated listing
```

**5. Unlock Listing from Review**
```
Endpoint: POST /api/inspector/listings/{listing_id}/unlock
Method:   POST
Purpose:  Unlock listing and revert status to PENDING
Path:     listing_id (Integer)
Effect:   
  - Unlock owner
  - Revert status to PENDING (if no decision yet)
Response: 200 OK with updated listing
```

**6. Approve Listing**
```
Endpoint: POST /api/inspector/listings/{listing_id}/approve
Method:   POST
Purpose:  Approve listing and change status to APPROVED
Path:     listing_id (Integer)
Effect:   
  - Status: REVIEWING → APPROVED
  - Record approval decision
  - Notify seller
Response: 200 OK with updated listing
```

**7. Reject Listing**
```
Endpoint: POST /api/inspector/listings/{listing_id}/reject
Method:   POST
Purpose:  Reject listing with reason code and text
Path:     listing_id (Integer)
Body:     {
  "reasonCode": "DUPLICATE|INVALID_INFO|LOW_QUALITY|INAPPROPRIATE|OTHER",
  "reasonText": "Detailed reason for rejection",
  "note": "Internal note (optional)"
}
Effect:   
  - Status: REVIEWING → REJECTED
  - Save rejection reason
  - Notify seller with reason
Response: 200 OK with updated listing
```

---

### S-24: Lịch Sử Duyệt

**8. Get Review History**
```
Endpoint: POST /api/inspector/reviews
Method:   POST
Purpose:  Get inspector's review history with date range filter
Query:    
  - from: YYYY-MM-DD (optional)
  - to: YYYY-MM-DD (optional)
  - page: 0 (default)
  - page_size: 10 (default)
Response: Page<ReviewHistory>
  {
    "content": [
      {
        "reviewId": 1,
        "listingId": 1,
        "inspectorId": 2,
        "decision": "APPROVED|REJECTED",
        "reasonCode": "...",
        "reviewedAt": "2026-01-31T...",
        ...
      }
    ],
    "totalElements": 50,
    ...
  }
```

**9. Get Review Detail (Optional)**
```
Endpoint: POST /api/inspector/reviews/detail
Method:   POST
Purpose:  Get detail of a specific review
Body:     {
  "reviewId": 1
}
Response: {
  "reviewId": 1,
  "listingId": 1,
  "inspectorId": 2,
  "decision": "APPROVED",
  "reasonCode": "...",
  "reasonText": "...",
  "note": "...",
  "reviewedAt": "2026-01-31T...",
  ...
}
```

---

### Dispute: Xử Lý Tranh Chấp

**10. Get Disputes List**
```
Endpoint: POST /api/inspector/disputes
Method:   POST
Purpose:  Get disputes for statistics and routing
Query:    
  - status: OPEN|RESOLVED (filter)
  - page: 0 (default)
  - page_size: 10 (default)
Response: Page<Dispute>
  {
    "content": [
      {
        "disputeId": 1,
        "listingId": 1,
        "buyerId": 3,
        "sellerId": 5,
        "status": "OPEN|RESOLVED",
        "createdAt": "2026-01-31T...",
        ...
      }
    ],
    "totalElements": 5,
    ...
  }
```

**11. Get Dispute Detail (Optional)**
```
Endpoint: POST /api/inspector/disputes/detail
Method:   POST
Purpose:  Get detail of a specific dispute
Body:     {
  "disputeId": 1
}
Response: {
  "disputeId": 1,
  "listingId": 1,
  "buyerId": 3,
  "sellerId": 5,
  "subject": "...",
  "description": "...",
  "status": "OPEN|RESOLVED",
  "createdAt": "2026-01-31T...",
  ...
}
```

---

## 📊 Endpoints by Screen/Feature

| Screen | Endpoint | Method | Purpose |
|--------|----------|--------|---------|
| **S-20** | /api/inspector/dashboard/stats | GET | Dashboard stats |
| **S-21** | /api/inspector/listings | POST | List for review |
| **S-22/S-23** | /api/inspector/listings/detail | POST | Review detail |
| **S-22** | /api/inspector/listings/{id}/lock | POST | Lock for review |
| **S-22** | /api/inspector/listings/{id}/unlock | POST | Unlock from review |
| **S-23** | /api/inspector/listings/{id}/approve | POST | Approve listing |
| **S-23** | /api/inspector/listings/{id}/reject | POST | Reject with reason |
| **S-24** | /api/inspector/reviews | POST | Review history |
| **S-24** | /api/inspector/reviews/detail | POST | Review detail (opt) |
| **Dispute** | /api/inspector/disputes | POST | Disputes list |
| **Dispute** | /api/inspector/disputes/detail | POST | Dispute detail (opt) |

---

## 🔄 Listing Status Flow

```
PENDING (submitted by seller)
  ↓
LOCK (by inspector)
  ↓ (change to REVIEWING)
REVIEWING (inspector reviewing)
  ↓
├─ APPROVE → APPROVED (published)
├─ REJECT → REJECTED (back to private)
└─ UNLOCK → PENDING (revert to pending)
```

---

## 🔑 Key Features

### Lock/Unlock System
- Lock: PENDING → REVIEWING (prevent seller editing)
- Unlock: REVIEWING → PENDING (if no decision yet)
- Prevents concurrent modifications

### Approval/Rejection
- Approve: REVIEWING → APPROVED
- Reject: REVIEWING → REJECTED (with reason)
- Reason code: DUPLICATE, INVALID_INFO, LOW_QUALITY, INAPPROPRIATE, OTHER

### Review History Tracking
- Track all review decisions
- Date range filter support
- Audit trail for compliance

### Dispute Management
- Track disputes for analytics
- Route to appropriate handlers
- Link to listings and users

---

## 📋 DTOs Required (To Create)

```
Request DTOs:
  ✅ GetDashboardStatsRequest (empty or inspectorId)
  ✅ GetListingsRequest (status, sort, page, pageSize)
  ✅ GetListingDetailRequest (listingId)
  ✅ LockListingRequest (listingId, inspectorId)
  ✅ UnlockListingRequest (listingId)
  ✅ ApproveListingRequest (listingId)
  ✅ RejectListingRequest (listingId, reasonCode, reasonText, note)
  ✅ GetReviewHistoryRequest (from, to, page, pageSize)
  ✅ GetReviewDetailRequest (reviewId)
  ✅ GetDisputesRequest (status, page, pageSize)
  ✅ GetDisputeDetailRequest (disputeId)

Response DTOs:
  ✅ InspectorDashboardStatsResponse
  ✅ ListingForReviewResponse
  ✅ ReviewHistoryResponse
  ✅ DisputeResponse
```

---

## 🏗️ Service Methods Required

```
InspectorService:
  ✅ getDashboardStats(inspectorId)
  ✅ getListingsForReview(status, sort, page, pageSize)
  ✅ getListingDetail(listingId)
  ✅ lockListing(listingId, inspectorId)
  ✅ unlockListing(listingId)
  ✅ approveListing(listingId)
  ✅ rejectListing(listingId, reasonCode, reasonText, note)
  ✅ getReviewHistory(inspectorId, from, to, page, pageSize)
  ✅ getReviewDetail(reviewId)
  ✅ getDisputes(status, page, pageSize)
  ✅ getDisputeDetail(disputeId)
```

---

## 🔐 Security/Validation

```
✅ Inspector role check (INSPECTOR role required)
✅ Listing status validation (correct state for operation)
✅ Lock ownership validation
✅ Date range validation (from <= to)
✅ Reason code validation
✅ Pagination validation (page >= 0, pageSize >= 1)
```

---

## 📊 Database Considerations

### New Tables Needed
```sql
-- Review/Decision History
CREATE TABLE review_decisions (
  review_id INT PRIMARY KEY,
  listing_id INT,
  inspector_id INT,
  decision ENUM('APPROVED', 'REJECTED'),
  reason_code VARCHAR(50),
  reason_text TEXT,
  note TEXT,
  reviewed_at TIMESTAMP,
  FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
  FOREIGN KEY (inspector_id) REFERENCES users(user_id)
);

-- Disputes
CREATE TABLE disputes (
  dispute_id INT PRIMARY KEY,
  listing_id INT,
  buyer_id INT,
  seller_id INT,
  subject VARCHAR(255),
  description TEXT,
  status ENUM('OPEN', 'RESOLVED'),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
  FOREIGN KEY (buyer_id) REFERENCES users(user_id),
  FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

-- Listing Lock (for concurrent editing prevention)
CREATE TABLE listing_locks (
  listing_id INT PRIMARY KEY,
  inspector_id INT,
  locked_at TIMESTAMP,
  FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
  FOREIGN KEY (inspector_id) REFERENCES users(user_id)
);
```

### Updates to Existing Tables
```sql
-- Add to bike_listings table
ALTER TABLE bike_listings ADD COLUMN locked_by INT;
ALTER TABLE bike_listings ADD COLUMN locked_at TIMESTAMP;
ALTER TABLE bike_listings ADD COLUMN reviewed_by INT;
ALTER TABLE bike_listings ADD COLUMN reviewed_at TIMESTAMP;
```

---

## 🧪 Test Cases

### S-20: Dashboard
- [ ] Get counts: PENDING, REVIEWING, APPROVED, REJECTED, DISPUTE

### S-21: List for Review
- [ ] List all (status=ALL)
- [ ] List PENDING only
- [ ] List REVIEWING only
- [ ] Sort newest/oldest
- [ ] Pagination

### S-22/S-23: Review Detail
- [ ] Get detail with all fields
- [ ] Lock listing (PENDING → REVIEWING)
- [ ] Unlock listing (REVIEWING → PENDING)
- [ ] Approve listing (REVIEWING → APPROVED)
- [ ] Reject with reason (REVIEWING → REJECTED)

### S-24: Review History
- [ ] Get all reviews
- [ ] Filter by date range
- [ ] Get single review detail
- [ ] Pagination

### Dispute
- [ ] List disputes
- [ ] Get dispute detail
- [ ] Filter by status

---

## 📁 File Created

**Location:** `C:\Users\phant\IdeaProjects\CycleX-BE\src\main\java\com\example\cyclexbe\controller\InspectorController.java`

**Status:** ✅ Controller skeleton with all endpoints listed (TODO for implementation)

---

## ⏭️ Next Steps

1. **Create InspectorService** - Implement all service methods
2. **Create DTOs** - Request/Response DTOs for all endpoints
3. **Create Repository Methods** - For review_decisions, disputes, listing_locks
4. **Add Database Tables** - Create new tables and alter existing ones
5. **Update SecurityConfig** - Allow /api/inspector/** endpoints for INSPECTOR role
6. **Implement Each Endpoint** - Service → Controller integration
7. **Create API Documentation** - Similar to Seller API doc
8. **Create Test Cases** - 20+ test scenarios

---

**Controller Status: ✅ Skeleton Complete**

*Ready for service and DTO implementation*

*Date: 2026-01-31*
