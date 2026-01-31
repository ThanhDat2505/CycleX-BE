# Seller API Testing Guide

**Date:** January 31, 2026  
**Version:** 1.0  
**Status:** Ready for Testing  

---

## 📋 Test Execution Plan

### Prerequisites

1. **Application Running**
   - Start Spring Boot app: `mvn spring-boot:run`
   - Server URL: `http://localhost:8080`

2. **Test Data Setup**
   ```sql
   -- Insert test seller (if not exists)
   INSERT INTO Users (email, password_hash, role, full_name, is_verify, status, created_at, updated_at)
   VALUES ('seller@test.com', 'hash123', 'SELLER', 'Test Seller', true, 'ACTIVE', NOW(), NOW());
   -- Note seller_id from insert result (assuming id=1)

   -- Insert test listings (sample approved and rejected)
   INSERT INTO bike_listings (seller_id, title, brand, model, price, status, created_at, updated_at, views_count)
   VALUES 
   (1, 'Honda CB150R', 'Honda', 'CB150R', 45000000, 'APPROVED', NOW(), NOW(), 120),
   (1, 'Yamaha YZF-R15', 'Yamaha', 'YZF-R15', 50000000, 'PENDING', NOW(), NOW(), 0),
   (1, 'Test Bike', 'Test', 'Test', 30000000, 'REJECTED', NOW(), NOW(), 0);
   ```

3. **Tools Needed**
   - Postman or Insomnia (for API testing)
   - Database client (MySQL Workbench, DBeaver, etc.)
   - Optional: curl (command line testing)

---

## 🧪 Test Cases & Execution

### Batch 1 Tests (Read-Only Operations)

#### Test 1.1: Dashboard Stats
```
Name: Get Dashboard Statistics
Expected Result: 200 OK with stats
Prerequisites: Seller with id=1 exists, has 1 APPROVED, 1 PENDING, 1 REJECTED listing

Request:
POST http://localhost:8080/api/seller/dashboard/stats
{
  "sellerId": 1
}

Expected Response (200 OK):
{
  "approvedCount": 1,
  "pendingCount": 1,
  "rejectedCount": 1,
  "totalListings": 3,
  "totalViews": 120
}

✓ PASS: Response matches expected format and values
✗ FAIL: Response differs or error returned
```

#### Test 1.2: Search Listings - No Filter
```
Name: List All Listings (No Filter)
Expected Result: 200 OK with all listings

Request:
POST http://localhost:8080/api/seller/listings/search
{
  "sellerId": 1,
  "page": 0,
  "pageSize": 10
}

Expected Response (200 OK):
- content array with 3 listings
- totalElements: 3
- totalPages: 1

✓ PASS: All 3 listings returned
✗ FAIL: Count doesn't match
```

#### Test 1.3: Search Listings - Filter by Status
```
Name: Search Listings by Status
Expected Result: 200 OK with only APPROVED listings

Request:
POST http://localhost:8080/api/seller/listings/search
{
  "sellerId": 1,
  "status": "APPROVED",
  "page": 0,
  "pageSize": 10
}

Expected Response (200 OK):
- content array with 1 listing
- totalElements: 1
- All listings have status: "APPROVED"

✓ PASS: Only APPROVED listings returned
✗ FAIL: Wrong listings or wrong count
```

#### Test 1.4: Search Listings - Filter by Brand
```
Name: Search Listings by Brand
Expected Result: 200 OK with Honda listings only

Request:
POST http://localhost:8080/api/seller/listings/search
{
  "sellerId": 1,
  "brand": "Honda",
  "page": 0,
  "pageSize": 10
}

Expected Response (200 OK):
- content array with 1 listing
- title: "Honda CB150R"
- brand: "Honda"

✓ PASS: Only Honda listings returned
✗ FAIL: Wrong listings or filtering not working
```

#### Test 1.5: Search Listings - Filter by Price Range
```
Name: Search Listings by Price Range
Expected Result: 200 OK with listings in price range

Request:
POST http://localhost:8080/api/seller/listings/search
{
  "sellerId": 1,
  "minPrice": 40000000,
  "maxPrice": 50000000,
  "page": 0,
  "pageSize": 10
}

Expected Response (200 OK):
- content array with Honda and Yamaha listings
- totalElements: 2
- All prices between 40M and 50M

✓ PASS: Only listings in price range returned
✗ FAIL: Wrong listings or price filter not working
```

#### Test 1.6: Listing Detail
```
Name: Get Listing Detail
Expected Result: 200 OK with complete listing

Request:
POST http://localhost:8080/api/seller/listings/detail
{
  "sellerId": 1,
  "listingId": 1
}

Expected Response (200 OK):
- All fields present (title, brand, model, price, etc.)
- listingId: 1
- status: APPROVED

✓ PASS: Complete listing details returned
✗ FAIL: Missing fields or wrong data
```

#### Test 1.7: Rejection Reason
```
Name: Get Rejection Reason
Expected Result: 200 OK for REJECTED listing

Request:
POST http://localhost:8080/api/seller/listings/rejection
{
  "sellerId": 1,
  "listingId": 3
}

Expected Response (200 OK):
- listingId: 3
- status: REJECTED

✓ PASS: Rejection reason returned
✗ FAIL: Error or wrong listing status

Test 1.7b: Error Case - Non-REJECTED Listing
Request same as above but with listingId: 1 (APPROVED)
Expected Response (400 BAD_REQUEST):
- error: "Listing is not rejected"

✓ PASS: Correct error returned
✗ FAIL: Wrong error or no error
```

---

### Batch 2 Tests (Create & Submit Operations)

#### Test 2.1: Create Draft Listing
```
Name: Create New Draft Listing
Expected Result: 201 CREATED with status=DRAFT

Request:
POST http://localhost:8080/api/seller/listings
{
  "sellerId": 1,
  "title": "New Test Bike",
  "bikeType": "Motorcycle",
  "brand": "Kawasaki",
  "model": "Ninja 250",
  "price": 60000000,
  "saveDraft": true
}

Expected Response (201 CREATED):
{
  "listingId": 4,
  "sellerId": 1,
  "title": "New Test Bike",
  "brand": "Kawasaki",
  "model": "Ninja 250",
  "price": 60000000,
  "status": "DRAFT",
  "viewsCount": 0,
  "createdAt": "2026-01-31T...",
  "updatedAt": "2026-01-31T..."
}

✓ PASS: Listing created with status=DRAFT
✗ FAIL: Status is not DRAFT or listing not created

Note: Record the listingId (4) for next tests
```

#### Test 2.2: Create & Auto-Submit Listing
```
Name: Create Listing with Auto-Submit
Expected Result: 201 CREATED with status=PENDING

Request:
POST http://localhost:8080/api/seller/listings
{
  "sellerId": 1,
  "title": "Auto-Submit Bike",
  "bikeType": "Motorcycle",
  "brand": "Suzuki",
  "model": "GSX-S125",
  "price": 35000000,
  "saveDraft": false
}

Expected Response (201 CREATED):
- status: "PENDING" (NOT DRAFT)

✓ PASS: Status is PENDING with saveDraft=false
✗ FAIL: Status is DRAFT or error returned
```

#### Test 2.3: Validation Error - Missing Required Field
```
Name: Create Listing Without Required Field
Expected Result: 400 BAD_REQUEST

Request:
POST http://localhost:8080/api/seller/listings
{
  "sellerId": 1,
  "bikeType": "Motorcycle",
  "brand": "Honda",
  "model": "CB150R",
  "price": 45000000
  // Missing: title
}

Expected Response (400 BAD_REQUEST):
{
  "error": "title: Title is required"
}

✓ PASS: Validation error for missing title
✗ FAIL: Listing created or different error
```

#### Test 2.4: Submit Draft for Approval
```
Name: Submit DRAFT Listing to PENDING
Expected Result: 200 OK with status=PENDING

Request:
POST http://localhost:8080/api/seller/listings/4/submit
{
  "sellerId": 1,
  "listingId": 4
}

Expected Response (200 OK):
{
  "listingId": 4,
  "status": "PENDING",
  "updatedAt": "2026-01-31T..." (more recent than createdAt)
}

✓ PASS: Status changed to PENDING
✗ FAIL: Status not PENDING or error returned
```

#### Test 2.5: Submit Non-DRAFT Error
```
Name: Try to Submit Already PENDING Listing
Expected Result: 400 BAD_REQUEST

Request:
POST http://localhost:8080/api/seller/listings/4/submit
{
  "sellerId": 1,
  "listingId": 4
}
(Same listing as Test 2.4, now status=PENDING)

Expected Response (400 BAD_REQUEST):
{
  "error": "Only DRAFT listings can be submitted"
}

✓ PASS: Error for submitting non-DRAFT
✗ FAIL: Listing submitted or different error
```

#### Test 2.6: Preview Listing
```
Name: Preview Draft Listing
Expected Result: 200 OK with full preview data

Request:
POST http://localhost:8080/api/seller/listings/preview
{
  "sellerId": 1,
  "listingId": 4
}

Expected Response (200 OK):
{
  "listingId": 4,
  "title": "New Test Bike",
  "brand": "Kawasaki",
  "model": "Ninja 250",
  "price": 60000000,
  "status": "PENDING",
  ... all fields ...
}

✓ PASS: Full preview returned
✗ FAIL: Missing fields or incomplete data
```

#### Test 2.7: List All Drafts
```
Name: Get All Draft Listings
Expected Result: 200 OK with paginated drafts

Create new draft first (repeat Test 2.1 with different data)

Request:
POST http://localhost:8080/api/seller/drafts
{
  "sellerId": 1,
  "page": 0,
  "pageSize": 10,
  "sort": "newest"
}

Expected Response (200 OK):
{
  "content": [
    {
      "listingId": 5,
      "title": "...",
      "status": "DRAFT",
      ...
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}

✓ PASS: Only DRAFT listings returned
✗ FAIL: Non-DRAFT listings included or count wrong
```

#### Test 2.8: Delete Draft
```
Name: Delete Draft Listing
Expected Result: 204 NO_CONTENT

Request:
DELETE http://localhost:8080/api/seller/drafts/5
{
  "sellerId": 1,
  "listingId": 5
}

Expected Response: 204 NO_CONTENT (empty body)

Verify: Query database
SELECT * FROM bike_listings WHERE listing_id = 5;
Result: No rows (hard delete)

✓ PASS: Draft deleted, no rows returned
✗ FAIL: Draft still exists or wrong status code
```

#### Test 2.9: Delete Non-DRAFT Error
```
Name: Try to Delete Non-DRAFT Listing
Expected Result: 400 BAD_REQUEST

Request:
DELETE http://localhost:8080/api/seller/drafts/4
{
  "sellerId": 1,
  "listingId": 4
}
(listing_id=4 is now PENDING)

Expected Response (400 BAD_REQUEST):
{
  "error": "Only DRAFT listings can be deleted"
}

✓ PASS: Error for deleting non-DRAFT
✗ FAIL: Listing deleted or different error
```

#### Test 2.10: Submit Draft from Drafts Endpoint
```
Name: Submit DRAFT from Drafts List
Expected Result: 200 OK with status=PENDING

Create new draft first (repeat Test 2.1)

Request:
POST http://localhost:8080/api/seller/drafts/6/submit
{
  "sellerId": 1,
  "listingId": 6
}

Expected Response (200 OK):
{
  "listingId": 6,
  "status": "PENDING"
}

✓ PASS: Status changed to PENDING
✗ FAIL: Status not PENDING or error
```

---

## 🧪 Error Test Cases Summary

### Required to Always Fail Gracefully

| Test Case | HTTP Method | Endpoint | Error Expected | HTTP Status |
|-----------|------------|----------|-----------------|------------|
| Missing sellerId | POST | /api/seller/dashboard/stats | "sellerId is required" | 400 |
| Seller not found | POST | /api/seller/dashboard/stats | "Seller not found" | 404 |
| Invalid status filter | POST | /api/seller/listings/search | "Invalid status" | 400 |
| Invalid page number | POST | /api/seller/listings/search | "Page must be >= 0" | 400 |
| Listing not found | POST | /api/seller/listings/detail | "Listing not found" | 404 |
| Rejection reason for approved | POST | /api/seller/listings/rejection | "Listing is not rejected" | 400 |
| Missing title on create | POST | /api/seller/listings | "Title is required" | 400 |
| Negative price | POST | /api/seller/listings | "Price must be >= 0" | 400 |
| Submit non-DRAFT | POST | /api/seller/listings/{id}/submit | "Only DRAFT can be submitted" | 400 |
| Delete non-DRAFT | DELETE | /api/seller/drafts/{id} | "Only DRAFT can be deleted" | 400 |

---

## 📊 Test Execution Checklist

### Batch 1 Tests
- [ ] Test 1.1: Dashboard Stats
- [ ] Test 1.2: Search No Filter
- [ ] Test 1.3: Search by Status
- [ ] Test 1.4: Search by Brand
- [ ] Test 1.5: Search by Price Range
- [ ] Test 1.6: Listing Detail
- [ ] Test 1.7: Rejection Reason (success + error)

### Batch 2 Tests
- [ ] Test 2.1: Create Draft
- [ ] Test 2.2: Create & Auto-Submit
- [ ] Test 2.3: Validation Error
- [ ] Test 2.4: Submit Draft
- [ ] Test 2.5: Submit Non-DRAFT Error
- [ ] Test 2.6: Preview Listing
- [ ] Test 2.7: List Drafts
- [ ] Test 2.8: Delete Draft
- [ ] Test 2.9: Delete Non-DRAFT Error
- [ ] Test 2.10: Submit from Drafts

### Error Cases
- [ ] All 10 error scenarios tested

---

## 🔧 How to Use Postman Collection

1. **Import Collection**
   - Download: `CycleX_Seller_API_Postman.json`
   - Postman → File → Import → Select file

2. **Set Variables**
   - Collection → Variables tab
   - Set `base_url` = `http://localhost:8080`
   - Set `seller_id` = `1` (your test seller)

3. **Run Requests**
   - Select folder: "Batch 1 - Read Operations"
   - Execute each request
   - Verify response matches documentation

4. **Run Test Suite**
   - Click "Run" button
   - Select all requests
   - Execute in sequence
   - View results

---

## 📝 Test Report Template

```
TEST EXECUTION REPORT
Date: [date]
Tester: [name]
Application: CycleX-BE Seller API
Version: 1.0

SUMMARY:
- Total Tests: 21
- Passed: __
- Failed: __
- Errors: __

BATCH 1 RESULTS:
  ✓/✗ Test 1.1: Dashboard Stats
  ✓/✗ Test 1.2: Search No Filter
  ✓/✗ Test 1.3: Search by Status
  ✓/✗ Test 1.4: Search by Brand
  ✓/✗ Test 1.5: Search by Price
  ✓/✗ Test 1.6: Listing Detail
  ✓/✗ Test 1.7: Rejection Reason

BATCH 2 RESULTS:
  ✓/✗ Test 2.1: Create Draft
  ✓/✗ Test 2.2: Create & Auto-Submit
  ✓/✗ Test 2.3: Validation Error
  ✓/✗ Test 2.4: Submit Draft
  ✓/✗ Test 2.5: Submit Non-DRAFT Error
  ✓/✗ Test 2.6: Preview Listing
  ✓/✗ Test 2.7: List Drafts
  ✓/✗ Test 2.8: Delete Draft
  ✓/✗ Test 2.9: Delete Non-DRAFT Error
  ✓/✗ Test 2.10: Submit from Drafts

ERROR CASES:
  ✓/✗ All 10 error scenarios

NOTES:
[Any issues found]

APPROVED BY: [QA Lead]
```

---

## 🚀 Quick Start

1. **Start Application**
   ```bash
   cd C:\Users\phant\IdeaProjects\CycleX-BE
   mvn spring-boot:run
   ```

2. **Import Postman Collection**
   - File → Import → `CycleX_Seller_API_Postman.json`

3. **Setup Test Data**
   ```sql
   -- Insert seller and listings (see prerequisites)
   ```

4. **Run Tests**
   - Start with Batch 1 (read operations)
   - Verify all pass
   - Move to Batch 2 (create/submit)
   - Test error scenarios

5. **Report Results**
   - Document in test report template
   - Note any failures
   - Report to dev team

---

**Document Version:** 1.0  
**Last Updated:** 2026-01-31  
**Status:** Ready for Testing
