# Batch 2 - Quick Reference Card

## 🎯 At a Glance

| Item | Details |
|------|---------|
| **Purpose** | Create & Submit Listings, Draft Management |
| **Endpoints** | 6 new endpoints |
| **DTOs** | 6 new request/response DTOs |
| **Service Methods** | 6 methods to add |
| **Status Update** | BikeListingStatus: 3 → 4 values (+DRAFT) |
| **Files to Create** | 6 DTO files |
| **Files to Update** | 3 existing files (enum, service, controller) |
| **Database Changes** | None (enum stored as string) |
| **Complexity** | Medium |
| **Time Estimate** | 2 hours |
| **Risk** | Low |

---

## 📍 Quick Endpoint List

```
1️⃣  POST   /api/seller/listings
     → Create listing as DRAFT
     → Input: CreateListingRequest
     → Output: BikeListingResponse (status=DRAFT)

2️⃣  POST   /api/seller/listings/{id}/submit
     → Submit DRAFT for approval
     → Input: SubmitListingRequest
     → Output: BikeListingResponse (status=PENDING)

3️⃣  GET    /api/seller/listings/{id}/preview
     → Preview listing before submit
     → Input: PreviewListingRequest
     → Output: PreviewListingResponse

4️⃣  GET    /api/seller/drafts
     → List all draft listings
     → Input: GetDraftsRequest
     → Output: Page<SellerListingResponse>

5️⃣  DELETE /api/seller/drafts/{id}
     → Delete draft listing
     → Input: DeleteDraftRequest
     → Output: 204 NO_CONTENT

6️⃣  POST   /api/seller/drafts/{id}/submit
     → Submit draft for approval
     → Input: SubmitDraftRequest
     → Output: BikeListingResponse (status=PENDING)
```

---

## 📝 DTO Quick Checklist

```
CreateListingRequest
  ✅ sellerId (Integer)
  ✅ title (String - required)
  ✅ price (BigDecimal - required)
  ✅ bikeType (String - required)
  ✅ brand (String - required)
  ✅ model (String - required)
  ⚪ description (String - optional)
  ⚪ manufactureYear (Integer - optional)
  ⚪ condition (String - optional)
  ⚪ usageTime (String - optional)
  ⚪ reasonForSale (String - optional)
  ⚪ locationCity (String - optional)
  ⚪ pickupAddress (String - optional)
  ⚪ saveDraft (Boolean - default true)

SubmitListingRequest
  ✅ sellerId
  ✅ listingId

PreviewListingResponse
  ✅ All listing fields (complete mapping)

GetDraftsRequest
  ✅ sellerId
  ⚪ page (default 0)
  ⚪ pageSize (default 10)
  ⚪ sort (default "newest")

DeleteDraftRequest
  ✅ sellerId
  ✅ listingId

SubmitDraftRequest
  ✅ sellerId
  ✅ listingId
```

---

## 🔄 Status Lifecycle

```
CREATE                SUBMIT                ADMIN REVIEW
   │                    │                        │
   ↓                    ↓                        ↓
DRAFT ────────────→ PENDING ───────→ APPROVED or REJECTED
  │                                        │
  │                                   REJECTED
  │                                        │
  └───────────────────────────────────────┘
       (can re-edit and resubmit - Batch 3)
```

---

## 🚨 Validation Rules

**Required for CREATE:**
- ✅ title (not blank, max 255)
- ✅ price (positive or zero)
- ✅ bikeType (not blank, max 50)
- ✅ brand (not blank, max 100)
- ✅ model (not blank, max 100)

**Required for SUBMIT:**
- ✅ Listing exists
- ✅ Seller owns it
- ✅ Status = DRAFT
- ✅ All required fields present

**Required for DELETE:**
- ✅ Listing exists
- ✅ Seller owns it
- ✅ Status = DRAFT

---

## 💾 Service Methods Signature

```java
// Create
public BikeListingResponse createListing(
    Integer sellerId, 
    CreateListingRequest req)

// Submit
public BikeListingResponse submitListing(
    Integer sellerId, 
    Integer listingId)

// Preview
public PreviewListingResponse previewListing(
    Integer sellerId, 
    Integer listingId)

// Get Drafts
public Page<SellerListingResponse> getDraftListings(
    Integer sellerId, 
    String sort, 
    int page, 
    int pageSize)

// Delete
public void deleteDraft(
    Integer sellerId, 
    Integer listingId)

// Helper (Private)
private void validateRequiredFields(
    CreateListingRequest req)
```

---

## 📍 Files Overview

### Create (6 New Files)
```
✅ CreateListingRequest.java (45 lines)
✅ SubmitListingRequest.java (20 lines)
✅ PreviewListingResponse.java (60 lines)
✅ GetDraftsRequest.java (30 lines)
✅ DeleteDraftRequest.java (20 lines)
✅ SubmitDraftRequest.java (20 lines)
   TOTAL: ~195 lines of DTO code
```

### Update (3 Existing Files)
```
📝 BikeListingStatus.java
   + Add DRAFT enum value (1 line)

📝 SellerService.java
   + 6 new methods (~120 lines)
   + 1 helper method (~15 lines)
   + Total: ~135 lines

📝 SellerController.java
   + 6 endpoints to implement (replace TODOs)
   + Total: ~70 lines modified
```

---

## 🎯 Implementation Order

```
1. Update BikeListingStatus enum (+1 line)
        ↓
2. Create PreviewListingResponse DTO
        ↓
3. Create CreateListingRequest DTO
        ↓
4. Create SubmitListingRequest DTO
        ↓
5. Create GetDraftsRequest DTO
        ↓
6. Create DeleteDraftRequest DTO
        ↓
7. Create SubmitDraftRequest DTO
        ↓
8. Add methods to SellerService
        ↓
9. Update SellerController endpoints
        ↓
10. Compile & Verify
```

---

## ✅ Acceptance Criteria

```
☑️ All 6 DTOs created with validation
☑️ BikeListingStatus includes DRAFT
☑️ All 6 service methods implemented
☑️ All 6 controller endpoints functional
☑️ Create listing defaults to DRAFT status
☑️ Can submit DRAFT → PENDING
☑️ Can preview any listing
☑️ Can list all drafts with pagination
☑️ Can delete only DRAFT listings
☑️ Ownership validation on all ops
☑️ Status validation before transitions
☑️ Maven compiles without errors
☑️ All HTTP status codes correct
☑️ Error messages are clear
☑️ Documentation updated
```

---

## 🧪 Quick Test Commands (Postman/Insomnia)

```bash
# 1. Create DRAFT listing
POST /api/seller/listings
{
  "sellerId": 1,
  "title": "Test Bike",
  "price": 45000000,
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "saveDraft": true
}

# 2. Submit for approval
POST /api/seller/listings/10/submit
{
  "sellerId": 1,
  "listingId": 10
}

# 3. Preview listing
POST /api/seller/listings/preview
{
  "sellerId": 1,
  "listingId": 10
}

# 4. List drafts
POST /api/seller/drafts
{
  "sellerId": 1,
  "page": 0,
  "pageSize": 10
}

# 5. Delete draft
POST /api/seller/drafts/delete
{
  "sellerId": 1,
  "listingId": 10
}
```

---

## 📋 Documentation Files

Located in project root:

1. **BATCH_2_PLAN.md** - 10 KB
   - Detailed technical specs
   - Endpoint definitions
   - Error scenarios
   - Test cases

2. **BATCH_2_VISUAL_WORKFLOW.md** - 12 KB
   - Architecture diagrams
   - Flow charts
   - DTO trees
   - Examples

3. **BATCH_1_VS_BATCH_2.md** - 8 KB
   - Comparison table
   - Use cases
   - Learning points
   - Roadmap

4. **BATCH_2_PREVIEW_SUMMARY.md** - 7 KB
   - High-level overview
   - Key decisions
   - Impact analysis
   - Next steps

5. **This file: BATCH_2_QUICK_REFERENCE.md** - 5 KB
   - At-a-glance reference
   - Quick checklists
   - Command examples

---

## 🚀 Ready to Start?

**Command to begin:**
```
"bắt đầu batch 2" 
hoặc 
"start batch 2"
```

**I will:**
1. Create all 6 DTOs
2. Update BikeListingStatus enum
3. Implement 6 service methods
4. Update 6 controller endpoints
5. Compile and verify
6. Report completion

**Expected delivery: 2-3 hours** ⏱️

---

## 📞 Quick Questions?

**Q: Can I create a listing without saving as draft?**  
A: Yes, set `saveDraft=false` to auto-submit (goes straight to PENDING)

**Q: Can I edit a listing after submitting?**  
A: No, that's Batch 3. For now, can only edit DRAFT listings.

**Q: What happens to deleted drafts?**  
A: Hard delete from database (can't recover)

**Q: Can I preview a PENDING or APPROVED listing?**  
A: Yes, can preview any status (no restrictions)

**Q: Do I need JWT token?**  
A: Not yet, sellerId in request body. JWT integration in Batch 3+

---

*Quick Reference for Batch 2 Implementation*  
*Last Updated: 2026-01-31*  
*Ready for Review ✅*
