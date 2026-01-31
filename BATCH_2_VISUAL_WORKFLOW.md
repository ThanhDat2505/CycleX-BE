# Batch 2 - Visual Architecture & Workflow

## 📊 Listing Lifecycle Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                      LISTING LIFECYCLE                          │
└─────────────────────────────────────────────────────────────────┘

   CREATE LISTING (POST /api/seller/listings)
          │
          ├─ saveDraft=true (default)
          │      │
          │      ↓
          │   DRAFT STATUS
          │      │
          │      ├─── EDIT PATCH /api/seller/listings/{id} (Batch 3)
          │      │      │
          │      │      └─→ Still DRAFT
          │      │
          │      ├─── PREVIEW GET /api/seller/listings/{id}/preview
          │      │
          │      └─── SUBMIT POST /api/seller/listings/{id}/submit
          │             │
          │             ↓
          │
          ├─ saveDraft=false
          │      │
          │      ↓
          │
          ▼─────────────────────────────────────────
        PENDING STATUS (Waiting for Admin Approval)
             │
             ├─── Admin Reviews
             │
             ├──────────────────┬──────────────────┐
             │                  │                  │
             ↓                  ↓                  ↓
          APPROVED          REJECTED            PENDING
         (Published)      (Can resubmit)       (Still waiting)
             │                 │
             ├──→ Views        │
             ├──→ Messages      └──→ Check rejection reason
             │                      (GET /api/seller/listings/{id}/rejection)
             │                      (Can re-edit and resubmit)
             │
             └──→ Can reactivate (Batch TBD)

DRAFT Management:
  ├─ GET /api/seller/drafts (List all drafts)
  ├─ DELETE /api/seller/drafts/{id} (Delete draft)
  └─ POST /api/seller/drafts/{id}/submit (Submit for approval)
```

---

## 🔄 API Endpoint Overview - Batch 2

### **Create Phase**
```
POST /api/seller/listings
├─ Request: CreateListingRequest
│  ├─ sellerId (required)
│  ├─ title (required)
│  ├─ price (required)
│  ├─ bikeType (required)
│  ├─ brand (required)
│  ├─ model (required)
│  ├─ Other fields (optional)
│  └─ saveDraft (true/false, default: true)
│
└─ Response: BikeListingResponse
   ├─ listingId (auto-generated)
   ├─ status: DRAFT (if saveDraft=true)
   └─ createdAt, updatedAt
```

### **Submit Phase**
```
POST /api/seller/listings/{id}/submit
├─ Request: SubmitListingRequest
│  ├─ sellerId (required)
│  └─ listingId (required)
│
└─ Response: BikeListingResponse
   ├─ status: PENDING (changed from DRAFT)
   └─ updatedAt (updated)
```

### **Preview Phase**
```
GET /api/seller/listings/{id}/preview
├─ Request: PreviewListingRequest (or use path param)
│  ├─ sellerId (required)
│  └─ listingId (required)
│
└─ Response: PreviewListingResponse
   └─ All listing fields for display
```

### **Draft Management Phase**
```
GET /api/seller/drafts
├─ Request: GetDraftsRequest
│  ├─ sellerId (required)
│  ├─ page (optional, default: 0)
│  ├─ pageSize (optional, default: 10)
│  └─ sort (optional, default: "newest")
│
└─ Response: Page<SellerListingResponse>
   └─ Paginated DRAFT listings

DELETE /api/seller/drafts/{id}
├─ Request: DeleteDraftRequest
│  ├─ sellerId (required)
│  └─ listingId (required)
│
└─ Response: 204 NO_CONTENT

POST /api/seller/drafts/{id}/submit
├─ Request: SubmitDraftRequest
│  ├─ sellerId (required)
│  └─ listingId (required)
│
└─ Response: BikeListingResponse
   └─ status: PENDING
```

---

## 🗂️ Files to Create/Update

### **Create Files (New DTOs)**
```
src/main/java/com/example/cyclexbe/dto/
├─ CreateListingRequest.java          ✅ NEW
├─ SubmitListingRequest.java          ✅ NEW
├─ PreviewListingResponse.java        ✅ NEW
├─ GetDraftsRequest.java              ✅ NEW
├─ DeleteDraftRequest.java            ✅ NEW
└─ SubmitDraftRequest.java            ✅ NEW
```

### **Update Files**
```
src/main/java/com/example/cyclexbe/
├─ domain/enums/BikeListingStatus.java
│  └─ Add: DRAFT status
│
├─ service/SellerService.java
│  ├─ Add: createListing()
│  ├─ Add: submitListing()
│  ├─ Add: previewListing()
│  ├─ Add: getDraftListings()
│  ├─ Add: deleteDraft()
│  ├─ Add: submitDraft() (or reuse submitListing)
│  └─ Add: validateRequiredFields() (helper)
│
├─ controller/SellerController.java
│  ├─ Update: createListing() - replace TODO
│  ├─ Update: submitListing() - replace TODO
│  ├─ Update: previewListing() - replace TODO
│  ├─ Update: getDrafts() - replace TODO
│  ├─ Update: deleteDraft() - replace TODO
│  └─ Update: submitDraft() - replace TODO
│
└─ security/SecurityConfig.java
   └─ Add: new endpoint authorizations (if needed)
```

---

## 🎯 DTO Structure Tree

```
CreateListingRequest
├─ sellerId: Integer @NotNull
├─ title: String @NotBlank @Size(255)
├─ description: String
├─ bikeType: String @NotBlank @Size(50)
├─ brand: String @NotBlank @Size(100)
├─ model: String @NotBlank @Size(100)
├─ manufactureYear: Integer
├─ condition: String @Size(50)
├─ usageTime: String @Size(100)
├─ reasonForSale: String
├─ price: BigDecimal @NotNull @PositiveOrZero
├─ locationCity: String @Size(100)
├─ pickupAddress: String
└─ saveDraft: Boolean = true

SubmitListingRequest
├─ sellerId: Integer @NotNull
└─ listingId: Integer @NotNull

PreviewListingResponse
├─ listingId: Integer
├─ sellerId: Integer
├─ title: String
├─ description: String
├─ bikeType: String
├─ brand: String
├─ model: String
├─ manufactureYear: Integer
├─ condition: String
├─ usageTime: String
├─ reasonForSale: String
├─ price: BigDecimal
├─ locationCity: String
├─ pickupAddress: String
├─ status: BikeListingStatus
├─ viewsCount: Integer
├─ createdAt: LocalDateTime
└─ updatedAt: LocalDateTime
    
GetDraftsRequest
├─ sellerId: Integer @NotNull
├─ sort: String = "newest"
├─ page: Integer @Min(0) = 0
└─ pageSize: Integer @Min(1) = 10

DeleteDraftRequest
├─ sellerId: Integer @NotNull
└─ listingId: Integer @NotNull

SubmitDraftRequest
├─ sellerId: Integer @NotNull
└─ listingId: Integer @NotNull
```

---

## 🧪 Request/Response Examples

### Example 1: Create DRAFT Listing
```json
REQUEST: POST /api/seller/listings
{
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "description": "Xe máy cũ, còn tốt",
  "manufactureYear": 2023,
  "condition": "Good",
  "usageTime": "2 years",
  "reasonForSale": "Muốn nâng cấp",
  "locationCity": "Ho Chi Minh",
  "pickupAddress": "123 Tran Hung Dao St",
  "saveDraft": true
}

RESPONSE: 201 CREATED
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
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

### Example 2: Submit DRAFT → PENDING
```json
REQUEST: POST /api/seller/listings/10/submit
{
  "sellerId": 1,
  "listingId": 10
}

RESPONSE: 200 OK
{
  "listingId": 10,
  "status": "PENDING",
  "updatedAt": "2026-01-31T19:05:00"
}
```

### Example 3: Preview Listing
```json
REQUEST: POST /api/seller/listings/preview
{
  "sellerId": 1,
  "listingId": 10
}

RESPONSE: 200 OK
{
  "listingId": 10,
  "sellerId": 1,
  "title": "Honda CB150R 2023",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000,
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T19:00:00",
  "updatedAt": "2026-01-31T19:05:00"
}
```

### Example 4: List Drafts
```json
REQUEST: POST /api/seller/drafts
{
  "sellerId": 1,
  "page": 0,
  "pageSize": 10,
  "sort": "newest"
}

RESPONSE: 200 OK
{
  "content": [
    {
      "listingId": 10,
      "title": "Honda CB150R 2023",
      "brand": "Honda",
      "price": 45000000,
      "status": "DRAFT",
      "viewsCount": 0,
      "createdAt": "2026-01-31T19:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

### Example 5: Validation Error
```json
REQUEST: POST /api/seller/listings
{
  "sellerId": 1,
  "title": "",  // ❌ blank
  "bikeType": "Motorcycle",
  "brand": "Honda"
  // ❌ missing: model, price
}

RESPONSE: 400 BAD_REQUEST
{
  "error": "Validation failed",
  "errors": [
    "title: must not be blank",
    "model: must not be blank",
    "price: must not be null"
  ]
}
```

---

## 🔐 Authorization Rules

```
POST /api/seller/listings
  ├─ Must be authenticated
  ├─ sellerId from request must match JWT token
  └─ Create listing for that seller

POST /api/seller/listings/{id}/submit
  ├─ Must be authenticated
  ├─ Seller must own the listing
  └─ Listing must be in DRAFT status

GET /api/seller/listings/{id}/preview
  ├─ Must be authenticated
  ├─ Seller must own the listing
  └─ Can preview own listings

GET /api/seller/drafts
  ├─ Must be authenticated
  ├─ sellerId from request must match JWT token
  └─ List own drafts only

DELETE /api/seller/drafts/{id}
  ├─ Must be authenticated
  ├─ Seller must own the draft
  └─ Draft must be DRAFT status

POST /api/seller/drafts/{id}/submit
  ├─ Must be authenticated
  ├─ Seller must own the draft
  └─ Draft must be DRAFT status
```

---

## 📊 Service Method Call Chain

```
Controller
  ↓
  ├─ @Valid @RequestBody CreateListingRequest
  │    ↓
  │    sellerService.createListing(sellerId, req)
  │         ├─ userRepository.findById(sellerId)
  │         ├─ validateRequiredFields(req)
  │         ├─ new BikeListing() + setBikeListing fields
  │         ├─ bikeListing.setStatus(DRAFT)
  │         ├─ bikeListingRepository.save()
  │         └─ return BikeListingResponse.from(saved)
  │
  ├─ @Valid @RequestBody SubmitListingRequest
  │    ↓
  │    sellerService.submitListing(sellerId, listingId)
  │         ├─ userRepository.findById(sellerId)
  │         ├─ bikeListingRepository.findByListingIdAndSeller()
  │         ├─ validateRequiredFields(listing)
  │         ├─ listing.setStatus(PENDING)
  │         ├─ bikeListingRepository.save()
  │         └─ return BikeListingResponse.from(saved)
  │
  └─ ...similar for other methods
```

---

## ⏱️ Timeline & Dependencies

```
BATCH 2 Tasks (Sequential)
├─ 1️⃣ Update BikeListingStatus enum [5 min]
│
├─ 2️⃣ Create DTOs [20 min]
│   ├─ CreateListingRequest
│   ├─ SubmitListingRequest
│   ├─ PreviewListingResponse
│   ├─ GetDraftsRequest
│   ├─ DeleteDraftRequest
│   └─ SubmitDraftRequest
│
├─ 3️⃣ Implement SellerService methods [30 min]
│   ├─ createListing()
│   ├─ submitListing()
│   ├─ previewListing()
│   ├─ getDraftListings()
│   ├─ deleteDraft()
│   ├─ validateRequiredFields() helper
│   └─ submitDraft()
│
├─ 4️⃣ Update SellerController endpoints [20 min]
│   ├─ POST /api/seller/listings
│   ├─ POST /api/seller/listings/{id}/submit
│   ├─ GET /api/seller/listings/{id}/preview
│   ├─ GET /api/seller/drafts
│   ├─ DELETE /api/seller/drafts/{id}
│   └─ POST /api/seller/drafts/{id}/submit
│
├─ 5️⃣ Compile & Verify [5 min]
│
└─ 6️⃣ Test & Document [15 min]

TOTAL: ~95 minutes
```

---

## ✅ Checklist for Implementation

- [ ] Update BikeListingStatus enum with DRAFT
- [ ] Create CreateListingRequest DTO
- [ ] Create SubmitListingRequest DTO
- [ ] Create PreviewListingResponse DTO
- [ ] Create GetDraftsRequest DTO
- [ ] Create DeleteDraftRequest DTO
- [ ] Create SubmitDraftRequest DTO
- [ ] Add createListing() to SellerService
- [ ] Add submitListing() to SellerService
- [ ] Add previewListing() to SellerService
- [ ] Add getDraftListings() to SellerService
- [ ] Add deleteDraft() to SellerService
- [ ] Add validateRequiredFields() helper to SellerService
- [ ] Update createListing() in SellerController
- [ ] Update submitListing() in SellerController
- [ ] Update previewListing() in SellerController
- [ ] Update getDrafts() in SellerController
- [ ] Update deleteDraft() in SellerController
- [ ] Update submitDraft() in SellerController
- [ ] Compile project
- [ ] Update SecurityConfig endpoints (if needed)
- [ ] Run manual tests
- [ ] Document any changes

---
