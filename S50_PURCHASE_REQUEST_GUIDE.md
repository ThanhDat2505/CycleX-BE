# S-50: Purchase Request Implementation Guide

## 📋 Overview

**Feature**: S-50 Purchase Request (Review & Confirm) - Buyer creates purchase request for bike listing
**Status**: ✅ Complete Backend Implementation
**Type**: PURCHASE only (not DEPOSIT/down payment)

---

## 🏗️ Architecture

### Entity Model
```
TransactionRequest (new)
├── listing (BikeListing) - ManyToOne
├── buyer (User) - ManyToOne
├── seller (User) - ManyToOne (derived from listing.seller)
├── transactionType: PURCHASE (enum)
├── status: PENDING_SELLER_CONFIRM (enum)
├── desiredTime: LocalDateTime
├── note: String (optional)
├── amount: BigDecimal (snapshot of listing.price)
└── timestamps: createdAt, updatedAt
```

### Enums
- **TransactionType**: PURCHASE, DEPOSIT
- **TransactionStatus**: PENDING_SELLER_CONFIRM, SELLER_CONFIRMED, REJECTED, CANCELLED, COMPLETED, EXPIRED

---

## 🔌 API Endpoints

### 1️⃣ GET /api/v1/listings/{listingId}/purchase-request/summary
**Purpose**: Load S-50 screen with precheck data
**Auth**: Required (BUYER role)
**Response**: PurchaseRequestSummaryResponse

```json
{
  "listingId": 1,
  "listingTitle": "Yamaha Road Bike",
  "listingPrice": 12500000,
  "listingStatus": "APPROVED",
  "buyerId": 2,
  "buyerEmail": "buyer@example.com",
  "buyerFullName": "Nguyen Van A",
  "buyerPhone": "0912345678",
  "sellerId": 1,
  "sellerEmail": "seller@example.com",
  "sellerFullName": "Pham Thi B",
  "sellerPhone": "0987654321",
  "estimatedFees": 0.00,
  "totalAmount": 12500000,
  "canCreateRequest": true,
  "message": "Ready to create purchase request"
}
```

### 2️⃣ POST /api/v1/listings/{listingId}/purchase-request
**Purpose**: Create purchase request after buyer confirmation
**Auth**: Required (BUYER role)
**Request Body**:
```json
{
  "desiredTime": "2026-03-15T10:00:00",
  "note": "Can deliver on weekend?"
}
```

**Response** (201 Created):
```json
{
  "message": "Purchase request created successfully",
  "transactionId": 1,
  "status": "PENDING_SELLER_CONFIRM",
  "transactionType": "PURCHASE",
  "amount": 12500000,
  "desiredTime": "2026-03-15T10:00:00",
  "note": "Can deliver on weekend?",
  "listing": {
    "listingId": 1,
    "title": "Yamaha Road Bike",
    "price": 12500000,
    "bikeType": "Road Bike",
    "brand": "Yamaha",
    "model": "YZF-R6",
    "locationCity": "Ho Chi Minh"
  },
  "buyer": {
    "userId": 2,
    "email": "buyer@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0912345678"
  },
  "seller": {
    "userId": 1,
    "email": "seller@example.com",
    "fullName": "Pham Thi B",
    "phone": "0987654321"
  },
  "createdAt": "2026-02-24T14:30:00"
}
```

---

## ✅ Business Rules Implemented

### Pre-Check Rules
1. ✅ Listing must exist
2. ✅ Listing must have status = APPROVED
3. ✅ Buyer must be authenticated (from JWT)
4. ✅ Seller must be associated with listing

### Input Validation Rules
5. ✅ `desiredTime` is required
6. ✅ `desiredTime` must be in the future
7. ✅ `desiredTime` cannot exceed 30 days from now (configurable in PurchaseRequestConstants)
8. ✅ `note` optional, max 500 characters

### Business Logic Rules
9. ✅ Create TransactionRequest with:
   - `transactionType` = PURCHASE (fixed)
   - `status` = PENDING_SELLER_CONFIRM (initial state)
   - `amount` = snapshot of listing.price at creation time
10. ✅ Prevent duplicate pending requests (same buyer + listing already pending → reject)
11. ✅ Race condition safe: Re-check listing.status immediately before save
12. ✅ Prevent buyer from purchasing own listing

### Error Handling
- 🔴 Listing not found → 400 Bad Request + LISTING_NOT_FOUND
- 🔴 Listing not approved → 409 Conflict + LISTING_NOT_APPROVED
- 🔴 Duplicate pending request → 409 Conflict + DUPLICATE_PENDING_REQUEST
- 🔴 Invalid desired time → 409 Conflict + DESIRED_TIME_INVALID
- 🔴 Buyer equals seller → 409 Conflict + BUYER_EQUALS_SELLER
- 🔴 Validation errors (missing fields) → 400 Bad Request + field errors

---

## 📂 Files Created

### Enums (2 files)
- `domain/enums/TransactionType.java` - PURCHASE, DEPOSIT
- `domain/enums/TransactionStatus.java` - PENDING_SELLER_CONFIRM, SELLER_CONFIRMED, etc.

### Entity (1 file)
- `entity/TransactionRequest.java` - Main entity for purchase request

### DTOs (3 files)
- `dto/CreatePurchaseRequestRequest.java` - Input for confirm endpoint
- `dto/PurchaseRequestSummaryResponse.java` - Response for summary endpoint
- `dto/CreatePurchaseRequestResponse.java` - Response for confirm endpoint

### Service (2 files)
- `service/PurchaseRequestService.java` - Interface
- `service/PurchaseRequestServiceImpl.java` - Implementation with all business logic

### Controller (1 file)
- `controller/PurchaseRequestController.java` - 2 endpoints

### Repository (1 file)
- `repository/TransactionRequestRepository.java` - JPA queries with custom methods

### Utilities (1 file)
- `util/PurchaseRequestConstants.java` - Constants (MAX_DESIRED_DAYS=30, etc.)

### Exception (1 file)
- `exception/PurchaseRequestException.java` - Custom exception

### Database (1 file)
- `resources/db/migration/V2_1__Create_TransactionRequest_Table.sql` - Table creation

### Configuration Updates (2 files)
- ✅ `exception/GlobalExceptionHandler.java` - Added PurchaseRequestException handler
- ✅ `security/SecurityConfig.java` - Added auth rules for S-50 endpoints

### Documentation (1 file)
- `CycleX_S50_PurchaseRequest_Postman.json` - Postman collection with examples

---

## 🔐 Security Implementation

### Authentication
- Buyer ID extracted from JWT token (SecurityContextHolder)
- Never trust buyerId from request body (injection safety)
- If not authenticated → 401 Unauthorized

### Authorization
- `GET /api/v1/listings/*/purchase-request/summary` → requires BUYER role
- `POST /api/v1/listings/*/purchase-request` → requires BUYER role

### SQL Injection Prevention
- JPA with parameterized queries
- No string concatenation in queries

---

## 🚀 How to Test

### Step 1: Run Postman Tests
1. Import `CycleX_S50_PurchaseRequest_Postman.json` to Postman
2. Set variables:
   - `base_url`: http://localhost:8080
   - `buyer_token`: Your JWT token for buyer user
   - `listing_id`: ID of an APPROVED listing

3. Run tests in order:
   - Test GET summary endpoint
   - Test POST create request (success)
   - Test POST with invalid data (duplicate, past time, etc.)

### Step 2: Run Unit Tests
```bash
mvn test -Dtest=PurchaseRequestServiceImplTest
mvn test -Dtest=PurchaseRequestControllerTest
```

### Step 3: Manual Testing
1. Create an approved listing
2. Login as different buyer
3. Call GET summary → verify can create
4. Call POST confirm → verify transaction created
5. Call POST again → verify duplicate prevention

---

## ⚙️ Configuration

### Constants in PurchaseRequestConstants.java
```java
MAX_DESIRED_DAYS = 30              // Max days in future for desiredTime
NOTE_MAX_LENGTH = 500              // Max characters for buyer's note
TRANSACTION_FEE_RATE_KEY = "..."   // Future: Fee configuration key
```

### To Change Max Days to 60:
```java
public static final int MAX_DESIRED_DAYS = 60;  // Change from 30
```

---

## 🔄 Integration with S-54 (Future)

S-50 creates TransactionRequest with `PENDING_SELLER_CONFIRM` status.

S-54 (Seller Review) will:
1. Query TransactionRequests where `seller_id = ? AND status = PENDING_SELLER_CONFIRM`
2. Display list to seller
3. Seller confirms/rejects → update status to SELLER_CONFIRMED/REJECTED
4. Trigger payment flow

Repository already supports:
```java
List<TransactionRequest> findByListing_ListingIdAndStatus(
    Integer listingId, 
    TransactionStatus status
);

List<TransactionRequest> findBySeller_UserId(Integer sellerId);
```

---

## 🐛 Edge Cases Handled

### Race Condition: Listing Status Change
**Scenario**: Buyer at S-50, just before confirming, listing rejected

**Solution**: Service re-checks `listing.status == APPROVED` immediately before creating transaction
- If status changed → PurchaseRequestException with clear message
- Transaction NOT created

### Concurrent Requests
**Scenario**: Buyer double-clicks confirm button

**Solution**: Unique constraint on (buyer_id, listing_id, status)
- Database prevents duplicate PENDING_SELLER_CONFIRM requests
- Second request gets: "You already have a pending purchase request"

### Listing Not Approved
**Scenario**: Listing still in REVIEWING, DRAFT, or REJECTED state

**Solution**: Pre-check blocks with specific error code
- FE can show "This listing is not available for purchase"
- Can retry after listing approved

### Buyer Equals Seller
**Scenario**: Seller tries to purchase own listing

**Solution**: Explicit check before creating transaction
- Error: "Seller cannot purchase their own listing"

---

## 📝 Notes & Assumptions

### Assumptions Made
1. **Estimated Fees**: Currently hardcoded to 0 (BigDecimal.ZERO)
   - Can be replaced with actual fee service later
   - Update `getPurchaseRequestSummary()` method

2. **User Role**: Assumes BUYER role enforced via SecurityConfig
   - Can extend to check other conditions if needed

3. **Timezone**: Using Java LocalDateTime (system timezone)
   - Should be consistent with frontend
   - Consider adding timezone support if needed

4. **Price Snapshot**: amount field stores listing.price at creation time
   - If listing price changes, transaction keeps original price
   - This is correct for audit trail

### Future Enhancements (TODO)
- [ ] Add fee calculation service
- [ ] Add payment integration (Stripe, Momo, etc.)
- [ ] Add notification service (email/SMS to seller)
- [ ] Add transaction history/timeline
- [ ] Add dispute resolution feature
- [ ] Add rating/review after completion
- [ ] Add refund policy handling
- [ ] Add guarantee/warranty management

---

## 🧪 Test Cases

### Success Cases
- ✅ Create request with valid data
- ✅ Create request with minimum data (no note)
- ✅ Multiple requests for different listings from same buyer
- ✅ Different buyers for same listing

### Failure Cases
- ❌ Listing not found
- ❌ Listing not approved (any other status)
- ❌ Duplicate pending request
- ❌ desiredTime in past
- ❌ desiredTime > 30 days away
- ❌ desiredTime null/missing
- ❌ Note > 500 characters
- ❌ Buyer = Seller
- ❌ Not authenticated (no JWT)
- ❌ Wrong role (not BUYER)

---

## 📦 Dependencies Used

- **Spring Boot 3.x**
- **Spring Web** (REST endpoints)
- **Spring Data JPA** (Database access)
- **Spring Security** (Authentication/Authorization)
- **Spring Validation** (@Valid, @NotNull, etc.)
- **Jakarta Persistence** (JPA annotations)
- **PostgreSQL** (Database)

---

## 🔗 Related Screens & Features

| Screen | Feature | Status |
|--------|---------|--------|
| S-50 | Purchase Request (Buyer confirms) | ✅ Complete |
| S-54 | Transaction Review (Seller confirms/rejects) | ⏳ Future |
| S-XX | Payment Processing | ⏳ Future |
| S-XX | Transaction History | ⏳ Future |
| S-XX | Dispute Resolution | ⏳ Future |

---

## 💡 Key Design Decisions

### Why TransactionRequest Entity Instead of PurchaseRequest?
- More generic name allows future extension (DEPOSIT, etc.)
- Repository methods support filtering by status
- Natural upgrade path to full transaction system

### Why Status = PENDING_SELLER_CONFIRM as Initial State?
- Clear workflow: PENDING → CONFIRMED → COMPLETED
- Prevents accidental payment before seller confirms
- Allows seller to reject unsuitable requests

### Why Snapshot Amount?
- If listing price changes, buyer sees consistent amount
- Matches real-world marketplace behavior (Amazon, eBay)
- Prevents "seller changes price after request" exploit

### Why No Timestamp for desiredTime Validation?
- desiredTime is business data (when buyer wants transaction)
- Separate from technical createdAt
- Allows flexibility: can set desired time for next month

---

Generated on: 2026-02-24
Version: 1.0

