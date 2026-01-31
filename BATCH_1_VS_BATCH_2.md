# Batch 1 vs Batch 2 - Feature Comparison

## 📊 Summary Table

| Feature | Batch 1 | Batch 2 | Notes |
|---------|---------|---------|-------|
| **Dashboard Stats** | ✅ GET | - | View approved/pending/rejected counts |
| **List Listings** | ✅ POST (search) | - | Filter by status, title, brand, model, price |
| **View Listing Detail** | ✅ POST | - | Get one listing details |
| **View Rejection Reason** | ✅ POST | - | See why listing was rejected |
| **Create Listing** | - | ✅ POST | New listing as DRAFT |
| **Submit Listing** | - | ✅ POST | DRAFT → PENDING (for approval) |
| **Preview Listing** | - | ✅ GET | Full preview before submit |
| **List Drafts** | - | ✅ GET | View all DRAFT listings |
| **Delete Draft** | - | ✅ DELETE | Remove unsent drafts |
| **Submit Draft** | - | ✅ POST | Submit DRAFT → PENDING |
| **Edit Listing** | Partial (placeholder) | - | Batch 3 planned |
| **Upload Images** | - | - | Batch 4 planned |

---

## 🔄 Status Support

### Batch 1 - Status Handling
```
BikeListingStatus enum (3 values):
  ├─ APPROVED (Read-only for seller)
  ├─ PENDING (Cannot modify)
  └─ REJECTED (Read-only, can see reason)
  
Operations:
  ├─ VIEW all listings (any status)
  ├─ VIEW dashboard stats (count by status)
  └─ SEARCH with status filter
```

### Batch 2 - Status Handling
```
BikeListingStatus enum (4 values):
  ├─ DRAFT (Editable, not visible to others)     ✅ NEW
  ├─ PENDING (Cannot modify, waiting approval)
  ├─ APPROVED (Published, public view)
  └─ REJECTED (Can resubmit)
  
Operations:
  ├─ CREATE with DRAFT status
  ├─ MANAGE drafts (list, delete, submit)
  ├─ SUBMIT DRAFT → PENDING
  ├─ PREVIEW before submit
  └─ VIEW rejection reason
```

---

## 🎯 Use Cases

### Batch 1 - Read-Only Seller Operations
```
Scenario: Seller views their published listings
┌─────────────────────────────────────────┐
│ 1. Seller opens dashboard               │
│    ↓                                    │
│ 2. See stats: 5 APPROVED, 2 PENDING,    │
│              1 REJECTED                 │
│    ↓                                    │
│ 3. View "My Listings" with filters      │
│    - Filter by: status, title, brand,   │
│      model, price, sort                 │
│    ↓                                    │
│ 4. Click on a listing to see detail     │
│    ↓                                    │
│ 5. If REJECTED, see rejection reason    │
└─────────────────────────────────────────┘

Endpoints Used (Batch 1):
  ✅ GET /api/seller/dashboard/stats
  ✅ POST /api/seller/listings/search
  ✅ POST /api/seller/listings/detail
  ✅ POST /api/seller/listings/rejection
```

### Batch 2 - Full Listing Lifecycle
```
Scenario 1: Create and publish new listing
┌──────────────────────────────────────────┐
│ 1. Seller fills listing form             │
│    ├─ Required: title, price, bikeType,  │
│    │             brand, model            │
│    └─ Optional: description, condition,  │
│                usageTime, etc.           │
│    ↓                                     │
│ 2. Click "Save as Draft"                 │
│    → Status = DRAFT                      │
│    ↓                                     │
│ 3. Review: Edit listing details (Batch 3) │
│    → Still DRAFT                         │
│    ↓                                     │
│ 4. Preview listing                       │
│    → See how it looks to buyers          │
│    ↓                                     │
│ 5. Click "Submit for Review"             │
│    → Status = PENDING                    │
│    ↓                                     │
│ 6. Wait for admin approval               │
│    → Admin approves → APPROVED           │
│    → Admin rejects → REJECTED            │
│       (See rejection reason - Batch 1)   │
│       (Re-edit and resubmit - Batch 3)   │
└──────────────────────────────────────────┘

Endpoints Used (Batch 2):
  ✅ POST /api/seller/listings (create)
  ✅ POST /api/seller/listings/preview
  ✅ POST /api/seller/listings/{id}/submit
  
Endpoints From Batch 1:
  ✅ POST /api/seller/listings/rejection

Scenario 2: Manage multiple drafts
┌──────────────────────────────────────────┐
│ 1. Seller wants to see all drafts        │
│    ↓                                     │
│ 2. GET /api/seller/drafts (paginated)    │
│    ↓                                     │
│ 3. For each draft:                       │
│    ├─ Click to edit (Batch 3)           │
│    ├─ Click to preview                   │
│    ├─ Click to submit                    │
│    └─ Click to delete                    │
└──────────────────────────────────────────┘

Endpoints Used (Batch 2):
  ✅ GET /api/seller/drafts
  ✅ DELETE /api/seller/drafts/{id}
  ✅ POST /api/seller/drafts/{id}/submit
```

---

## 📋 DTOs Overview

### Batch 1 DTOs (Read/Search)
```
Input DTOs:
  ├─ GetDashboardStatsRequest
  ├─ GetListingsRequest (with search filters)
  ├─ GetListingDetailRequest
  └─ (no input for rejection endpoint)

Output DTOs:
  ├─ SellerDashboardStatsResponse
  ├─ Page<SellerListingResponse>
  └─ SellerListingResponse (for detail & rejection)
```

### Batch 2 DTOs (Create/Submit/Draft)
```
Input DTOs:
  ├─ CreateListingRequest (required: title, price, bikeType, brand, model)
  ├─ SubmitListingRequest
  ├─ PreviewListingRequest (same as detail)
  ├─ GetDraftsRequest
  ├─ DeleteDraftRequest
  └─ SubmitDraftRequest

Output DTOs:
  ├─ BikeListingResponse (for create)
  ├─ BikeListingResponse (for submit)
  ├─ PreviewListingResponse (for preview)
  ├─ Page<SellerListingResponse> (for drafts list)
  └─ (no body for delete)
```

---

## 🔐 Authorization & Ownership

### Batch 1
```
All endpoints:
  ├─ Require: sellerId in request body
  ├─ Validate: Seller exists
  └─ Validate: Seller owns the listing
  
No JWT extraction from token (yet)
- sellerId passed explicitly in request body
```

### Batch 2
```
All endpoints:
  ├─ Require: sellerId in request body
  ├─ Validate: Seller exists
  ├─ Validate: Seller owns the resource (for listing ops)
  └─ Validate: Status transitions are valid
  
Same pattern as Batch 1
- JWT extraction recommended for future (Batch 3+)
```

---

## 🧪 Testing Workflow

### Batch 1 Testing
```
Test Suite:
  ├─ Dashboard Stats
  │   └─ Verify counts for each status
  │
  ├─ Search Listings
  │   ├─ Filter by status
  │   ├─ Filter by title (case-insensitive)
  │   ├─ Filter by brand
  │   ├─ Filter by model
  │   ├─ Filter by price range
  │   ├─ Combine multiple filters
  │   ├─ Pagination
  │   └─ Sorting (newest/oldest)
  │
  ├─ View Detail
  │   └─ Get full listing details
  │
  └─ View Rejection
      └─ See rejection reason if status=REJECTED

All tests: Read-only operations
```

### Batch 2 Testing
```
Test Suite:
  ├─ Create Listing
  │   ├─ Create with required fields only
  │   ├─ Create with all fields
  │   ├─ Validation: missing required fields
  │   └─ Verify status=DRAFT
  │
  ├─ Submit Listing
  │   ├─ Submit DRAFT listing
  │   ├─ Validation: cannot submit non-DRAFT
  │   ├─ Validation: missing required fields
  │   └─ Verify status=PENDING
  │
  ├─ Preview Listing
  │   ├─ Preview DRAFT listing
  │   ├─ Preview any status listing
  │   └─ See all fields
  │
  ├─ List Drafts
  │   ├─ Get all drafts for seller
  │   ├─ Pagination
  │   ├─ Sorting (newest/oldest)
  │   └─ Filter by status (should only return DRAFT)
  │
  ├─ Delete Draft
  │   ├─ Delete DRAFT listing
  │   ├─ Validation: cannot delete non-DRAFT
  │   └─ Verify hard delete
  │
  └─ Submit Draft
      ├─ Submit DRAFT from drafts endpoint
      ├─ Verify status=PENDING
      └─ Same validation as submitListing

All tests: Create, update, delete operations
```

---

## 🗺️ Implementation Roadmap

```
┌─────────────────────────────────────────┐
│         BATCH 1 (COMPLETE)              │
├─────────────────────────────────────────┤
│ ✅ Dashboard Stats                      │
│ ✅ List Listings (with filters)         │
│ ✅ View Detail                          │
│ ✅ View Rejection Reason                │
│ ✅ Enhanced Filtering (title, brand...)  │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         BATCH 2 (PLANNED)               │
├─────────────────────────────────────────┤
│ ⏳ Create Listing (DRAFT)               │
│ ⏳ Submit for Approval (PENDING)        │
│ ⏳ Preview Listing                      │
│ ⏳ List Drafts                          │
│ ⏳ Delete Draft                         │
│ ⏳ Submit Draft                         │
│ ⏳ Update BikeListingStatus (add DRAFT) │
│ ⏳ Validation Rules                     │
│ ⏳ Error Handling                       │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         BATCH 3 (FUTURE)                │
├─────────────────────────────────────────┤
│ ⏳ Edit/Update Listing                  │
│ ⏳ Edit only DRAFT listings             │
│ ⏳ Validation for edit                  │
│ ⏳ Reactivate Rejected listings         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         BATCH 4 (FUTURE)                │
├─────────────────────────────────────────┤
│ ⏳ Upload Images                        │
│ ⏳ Delete Images                        │
│ ⏳ Retry Failed Upload                  │
│ ⏳ Image Validation                     │
└─────────────────────────────────────────┘
```

---

## 💾 Database Changes

### Batch 1
```
Changes: NONE
- Uses existing BikeListing entity
- Uses existing BikeListingStatus enum (3 values)
- Uses existing repository queries
```

### Batch 2
```
Changes: ADD DRAFT status
- BikeListingStatus enum: 3 → 4 values
- No schema changes needed (enum stored as string)
- No new fields in BikeListing table
- Optional: Add indexes for better query performance

Recommended Indexes:
  CREATE INDEX idx_bike_listings_seller_status 
    ON bike_listings(seller_id, status);
```

---

## 📊 Comparison Matrix

| Aspect | Batch 1 | Batch 2 |
|--------|---------|---------|
| **Purpose** | View & Search listings | Create & Submit listings |
| **Operations** | Read (GET/POST search) | Create, Update, Delete |
| **Endpoints** | 4 | 6 |
| **DTOs Created** | 4 request DTOs | 6 request DTOs |
| **Status Support** | 3 values | 4 values (+DRAFT) |
| **Schema Changes** | None | Enum update only |
| **Service Methods** | 4 query methods | 6 CRUD methods |
| **Complexity** | Medium | Medium-High |
| **Test Cases** | ~15 | ~25 |
| **Est. Time** | 2 hours | 2 hours |

---

## 🎓 Learning Points

### Batch 1 Teaches
- Dynamic filtering with JPA Specifications
- Multiple filter criteria (AND logic)
- Case-insensitive search
- Pagination and sorting
- DTO mapping and validation
- Error handling for queries

### Batch 2 Teaches
- Listing lifecycle management (DRAFT → PENDING → APPROVED/REJECTED)
- Status validation and transitions
- Form submission workflows
- Required vs optional field validation
- Resource ownership checks
- Preview/preview pattern
- Soft delete alternatives (will explore in future)

---

## ✨ Key Differences

### Read Operations (Batch 1)
```
Simple GET-like operations
  └─ No state changes
  └─ No business logic validation
  └─ Can be called multiple times safely (idempotent)
```

### Write Operations (Batch 2)
```
Complex state transitions
  ├─ DRAFT created → status changes
  ├─ Validation before transition
  ├─ Side effects (updatedAt changes)
  └─ Not idempotent - can't safely repeat
```

---

## 🚀 After Batch 2 is Complete

You'll have:
✅ Full CRUD for sellers (Create, Read, Update-pending, Delete-draft)
✅ Listing lifecycle support (DRAFT → PENDING → APPROVED/REJECTED)
✅ Draft management (edit, preview, delete before submit)
✅ Comprehensive error handling
✅ Validation at multiple levels

Ready for:
⏳ Image upload system (Batch 4)
⏳ JWT authentication integration (security enhancement)
⏳ Advanced features (favorites, messages, transactions)

---
