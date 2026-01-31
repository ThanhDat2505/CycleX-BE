# Batch 2 Preview - Complete Overview

📅 **Date:** January 31, 2026  
📌 **Status:** Planning Phase (Ready for Review)  
✅ **Batch 1:** Complete  
⏳ **Batch 2:** Awaiting Approval

---

## 📋 What's in the Preview?

I've created 4 comprehensive planning documents for Batch 2:

### 1. **BATCH_2_PLAN.md** - Detailed Technical Specifications
- ✅ Complete endpoint specifications
- ✅ Request/Response DTOs with validation rules
- ✅ Service method signatures
- ✅ Error handling scenarios
- ✅ Database impact analysis
- ✅ Test cases to cover
- ✅ Implementation sequence

### 2. **BATCH_2_VISUAL_WORKFLOW.md** - Architecture & Diagrams
- ✅ Listing lifecycle flow diagram
- ✅ API endpoint overview
- ✅ Files to create/update
- ✅ DTO structure tree
- ✅ Request/response examples
- ✅ Authorization rules
- ✅ Service call chains
- ✅ Timeline & checklist

### 3. **BATCH_1_VS_BATCH_2.md** - Comparison & Context
- ✅ Feature comparison table
- ✅ Use case scenarios
- ✅ DTOs overview
- ✅ Authorization differences
- ✅ Testing workflow comparison
- ✅ Implementation roadmap (Batch 3, 4 preview)

---

## 🎯 Batch 2 Summary

### **6 New Endpoints**

```
S-12: Create Listing
  POST /api/seller/listings
  └─ Create new listing as DRAFT

S-12: Submit for Approval  
  POST /api/seller/listings/{id}/submit
  └─ DRAFT → PENDING status

S-14: Preview Listing
  GET /api/seller/listings/{id}/preview
  └─ Preview full listing data before submit

S-18: List Drafts
  GET /api/seller/drafts
  └─ View all DRAFT listings (paginated)

S-18: Delete Draft
  DELETE /api/seller/drafts/{id}
  └─ Remove unsent draft

S-18: Submit Draft
  POST /api/seller/drafts/{id}/submit
  └─ Submit DRAFT → PENDING for approval
```

---

## 🔄 Status Updates

### BikeListingStatus Enum
```
BEFORE (3 values):           AFTER (4 values):
  ├─ APPROVED               ├─ DRAFT        ✅ NEW
  ├─ PENDING                ├─ PENDING
  └─ REJECTED               ├─ APPROVED
                            └─ REJECTED
```

---

## 📊 DTOs to Create (6 files)

```
1. CreateListingRequest
   ├─ Required fields: title, price, bikeType, brand, model
   ├─ Optional fields: description, condition, etc.
   └─ saveDraft: Boolean (true/false)

2. SubmitListingRequest
   ├─ sellerId
   └─ listingId

3. PreviewListingResponse
   └─ All listing fields for display

4. GetDraftsRequest
   ├─ sellerId
   ├─ page, pageSize
   └─ sort

5. DeleteDraftRequest
   ├─ sellerId
   └─ listingId

6. SubmitDraftRequest
   ├─ sellerId
   └─ listingId
```

---

## 🔧 Service Methods to Add (6 methods)

```
1. createListing(sellerId, CreateListingRequest)
   └─ Returns: BikeListingResponse with status=DRAFT

2. submitListing(sellerId, listingId)
   └─ Returns: BikeListingResponse with status=PENDING

3. previewListing(sellerId, listingId)
   └─ Returns: PreviewListingResponse (full data)

4. getDraftListings(sellerId, sort, page, pageSize)
   └─ Returns: Page<SellerListingResponse>

5. deleteDraft(sellerId, listingId)
   └─ Returns: void (204 NO_CONTENT)

6. validateRequiredFields(CreateListingRequest) [HELPER]
   └─ Validates: title, price, bikeType, brand, model
```

---

## 📈 Files to Modify/Create

```
FILES TO CREATE (6 DTOs):
  ✅ CreateListingRequest.java
  ✅ SubmitListingRequest.java
  ✅ PreviewListingResponse.java
  ✅ GetDraftsRequest.java
  ✅ DeleteDraftRequest.java
  ✅ SubmitDraftRequest.java

FILES TO UPDATE:
  📝 BikeListingStatus.java (add DRAFT)
  📝 SellerService.java (add 6 methods)
  📝 SellerController.java (update 6 endpoints)
  📝 SecurityConfig.java (authorize endpoints - if needed)

TOTAL: 9 files (6 new + 3 existing)
```

---

## ✅ Validation Rules

| Field | Create | Submit | Preview | Delete |
|-------|--------|--------|---------|--------|
| **title** | ✅ Required | - | - | - |
| **price** | ✅ Required | - | - | - |
| **bikeType** | ✅ Required | - | - | - |
| **brand** | ✅ Required | - | - | - |
| **model** | ✅ Required | - | - | - |
| **description** | Optional | - | - | - |
| **Seller exists** | ✅ Check | ✅ Check | ✅ Check | ✅ Check |
| **Owns resource** | N/A | ✅ Check | ✅ Check | ✅ Check |
| **Status=DRAFT** | N/A | ✅ Check | N/A | ✅ Check |

---

## 🚀 Implementation Flow

```
Step 1: Update BikeListingStatus enum
  └─ Add: DRAFT status

Step 2: Create all DTOs (6 files)
  └─ With validation annotations

Step 3: Implement SellerService (6 methods)
  ├─ createListing()
  ├─ submitListing()
  ├─ previewListing()
  ├─ getDraftListings()
  ├─ deleteDraft()
  └─ validateRequiredFields() [helper]

Step 4: Update SellerController (6 endpoints)
  ├─ POST /api/seller/listings
  ├─ POST /api/seller/listings/{id}/submit
  ├─ GET /api/seller/listings/{id}/preview
  ├─ GET /api/seller/drafts
  ├─ DELETE /api/seller/drafts/{id}
  └─ POST /api/seller/drafts/{id}/submit

Step 5: Compile & Test
  └─ Verify no errors

Step 6: Manual Testing
  └─ Test all scenarios
```

**Estimated Time: ~2 hours**

---

## 🧪 Test Scenarios (Key Cases)

### ✅ Positive Cases
```
1. Create listing with required fields only
   → Status = DRAFT ✓

2. Create listing with all fields
   → Status = DRAFT ✓

3. Submit DRAFT listing
   → Status changes to PENDING ✓

4. Preview DRAFT listing
   → See all fields ✓

5. List all drafts with pagination
   → Get DRAFT listings only ✓

6. Delete DRAFT listing
   → Removed from database ✓

7. Submit draft from drafts endpoint
   → Status changes to PENDING ✓
```

### ❌ Negative Cases
```
1. Create without required fields
   → 400 BAD_REQUEST ✓

2. Submit non-existent listing
   → 404 NOT_FOUND ✓

3. Submit non-DRAFT listing
   → 400 BAD_REQUEST ✓

4. Delete non-DRAFT listing
   → 400 BAD_REQUEST ✓

5. Seller trying to access other seller's listing
   → Validation check in service ✓

6. Invalid pagination parameters
   → 400 BAD_REQUEST ✓
```

---

## 📌 Key Decisions Made

### ✅ DRAFT Status Added
- Listing lifecycle: DRAFT → PENDING → APPROVED/REJECTED
- Allows sellers to save incomplete listings
- No visibility to other users until APPROVED

### ✅ Required Fields for Creation
- **Required:** title, price, bikeType, brand, model
- **Optional:** everything else
- Encourages complete listings but allows flexible flow

### ✅ Preview Before Submit
- Sellers see exactly how listing will appear
- Can identify missing/incorrect fields before submitting
- Reduces admin review burden

### ✅ Draft Management
- View all drafts with pagination
- Delete unused drafts
- Submit from drafts list or detail page

### ✅ Validation Approach
- Use @NotNull, @NotBlank, @Size annotations on DTOs
- Framework auto-validates before reaching service
- Service validates business rules (status, ownership)

---

## 🔐 Security Aspects

```
Authentication:
  ├─ All endpoints require authenticated user
  ├─ sellerId in request body (for now)
  └─ Future: Extract from JWT token

Authorization:
  ├─ Sellers can only manage own listings
  ├─ Can only submit DRAFT listings
  ├─ Can only delete DRAFT listings
  └─ Validation in service layer

Data Privacy:
  ├─ DRAFT listings are private (seller only)
  ├─ PENDING listings are hidden from public
  ├─ APPROVED listings are public
  └─ REJECTED listings are private (seller + admin)
```

---

## 📊 Impact Analysis

### What Stays the Same (from Batch 1)
```
✅ BikeListingRepository structure
✅ BikeListing entity schema
✅ SellerListingResponse DTO
✅ BikeListingResponse DTO
✅ Existing endpoints (GET, search, detail, rejection)
✅ SecurityConfig routes
```

### What Changes
```
📝 BikeListingStatus enum (add DRAFT)
📝 SellerService (add 6 methods)
📝 SellerController (implement 6 endpoints)
✅ New DTOs (6 files)
```

### No Database Schema Changes Required
```
- DRAFT status stored as string in existing VARCHAR column
- No new tables needed
- No new columns needed
- All existing data remains valid
```

---

## 🎓 Architectural Patterns Used

### 1. **DTO Validation Pattern**
- @Valid annotation on controller parameters
- Validation annotations on DTO fields (@NotNull, @Size, etc.)
- Framework auto-validates before service processing

### 2. **Service Layer Pattern**
- All business logic in service layer
- Controller is thin (just delegates to service)
- Service validates ownership and state transitions

### 3. **Response Mapping Pattern**
- Static `from(entity)` methods in response DTOs
- Centralized mapping logic
- Easy to maintain and update

### 4. **Resource Ownership Pattern**
- Check seller exists
- Check listing belongs to seller
- Prevent cross-seller access

### 5. **State Transition Pattern**
- Only allow specific transitions (DRAFT → PENDING)
- Validate state before transition
- Update timestamp on transition

---

## 📚 Documentation Files Created

```
BATCH_2_PLAN.md (10 KB)
  └─ Technical specifications, endpoints, DTOs, validation rules

BATCH_2_VISUAL_WORKFLOW.md (12 KB)
  └─ Architecture diagrams, examples, flow charts, checklist

BATCH_1_VS_BATCH_2.md (8 KB)
  └─ Comparison, use cases, learning points, roadmap

THIS FILE: Batch 2 Preview Summary (7 KB)
  └─ Overview, key points, decision summary
```

**Total: 37 KB of documentation for preview review**

---

## ✨ Ready to Implement?

Before starting Batch 2, please review:

1. ✅ **BATCH_2_PLAN.md** - Understand what needs to be built
2. ✅ **BATCH_2_VISUAL_WORKFLOW.md** - See the architecture
3. ✅ **BATCH_1_VS_BATCH_2.md** - Understand the differences
4. ✅ **This file** - Get the high-level overview

Then confirm:
- [ ] Requirements are clear
- [ ] Endpoints look correct
- [ ] DTOs have right fields
- [ ] Validation rules make sense
- [ ] Timeline is acceptable

---

## 🎯 Next Steps

**If you want to proceed with Batch 2:**

```
1. Review the 3 planning documents
2. Provide feedback/changes if needed
3. Approve to proceed
4. I'll implement all 6 endpoints
5. Compile and verify
6. You test and report results
7. Move to Batch 3 or other features
```

**If you want modifications:**

```
Suggest changes to:
  ├─ Endpoint specifications
  ├─ DTO fields
  ├─ Validation rules
  ├─ Error handling
  └─ Any other aspect

I'll update the plan and create new version
```

---

## 📞 Summary

**Batch 2 will add:**
- ✅ 6 new REST endpoints
- ✅ 6 request/response DTOs
- ✅ 6 service methods
- ✅ Listing creation workflow
- ✅ Draft management system
- ✅ DRAFT status to lifecycle

**Benefits:**
- ✅ Sellers can save incomplete listings
- ✅ Flexible submission workflow
- ✅ Preview before publishing
- ✅ Draft management
- ✅ Clear status transitions

**Complexity:** Medium  
**Estimated Time:** 2 hours  
**Risk Level:** Low (no database schema changes)  

---

## 🚀 Ready?

**Type:** "bắt đầu batch 2" or "start batch 2"  
**When:** After you review the planning documents

I'll wait for your approval to proceed! 🎯

---

*Created: 2026-01-31*  
*Last Updated: 2026-01-31*  
*Status: PREVIEW (Awaiting Review)*
