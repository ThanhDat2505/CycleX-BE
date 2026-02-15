# S-40: Inspection Chat Thread with ARCHIVED Lock

## Overview

This document describes the implementation of **lock mechanism** for Inspection Chat Threads when a listing is ARCHIVED.

### Key Features:
1. ✅ GET chat thread + messages → Always return 200 (even if locked) with `locked=true`
2. ✅ POST text message → Return 423 LOCKED if listing ARCHIVED
3. ✅ POST upload image → Return 423 LOCKED if listing ARCHIVED
4. ✅ POST mark read → Always allowed (lock doesn't affect this)

---

## 1. API Endpoints

### S-40.1: Load Chat Thread + Messages

**Request:**
```
GET /api/inspection-requests/{requestId}/chat-thread

Authorization: Bearer JWT
```

**Response (200 OK):**
```json
{
  "threadId": 12,
  "requestId": 9,
  "listingId": 13,
  "listingTitle": "Giant Escape 2022",
  "locked": true,
  "lockedReason": "LISTING_ARCHIVED",
  "messages": [
    {
      "id": 101,
      "senderId": 10,
      "senderName": "Seller A",
      "senderRole": "SELLER",
      "type": "TEXT",
      "text": "Em gửi xe nhờ kiểm định",
      "attachmentUrl": null,
      "attachmentCaption": null,
      "createdAt": "2026-02-15T10:30:00"
    }
  ],
  "createdAt": "2026-02-15T10:00:00",
  "updatedAt": "2026-02-15T10:30:00"
}
```

**Permission Rules:**
- INSPECTOR: must be assigned to this inspection request
- SELLER: must be owner of the listing
- Returns 403 if user doesn't have access

**Lock Status:**
- `locked=true` + `lockedReason="LISTING_ARCHIVED"` if `listing.status == ARCHIVED`
- `locked=false` + `lockedReason=null` otherwise
- **Important:** Still return 200, allowing users to read past messages

---

### S-40.2: Send TEXT Message

**Request:**
```
POST /api/inspection-requests/{requestId}/chat-messages

Authorization: Bearer JWT
Content-Type: application/json

{
  "type": "TEXT",
  "text": "Anh cần thêm ảnh số khung số sườn."
}
```

**Response (200 OK):**
```json
{
  "id": 102,
  "senderId": 2,
  "senderName": "Inspector B",
  "senderRole": "INSPECTOR",
  "type": "TEXT",
  "text": "Anh cần thêm ảnh số khung số sườn.",
  "attachmentUrl": null,
  "attachmentCaption": null,
  "createdAt": "2026-02-15T10:35:00"
}
```

**Error Response (423 LOCKED):**
```json
{
  "status": 423,
  "message": "Chat is locked because listing is ARCHIVED"
}
```

**Other Error Responses:**
- 404: Inspection request not found
- 403: User doesn't have access
- 400: Empty message text

**Validation:**
1. Message text cannot be empty
2. Permission check (INSPECTOR or SELLER)
3. **Lock check:** If `listing.status == ARCHIVED` → throw 423

---

### S-40.3: Upload Image + Create IMAGE Message

**Request:**
```
POST /api/inspection-requests/{requestId}/chat-messages:upload

Authorization: Bearer JWT
Content-Type: multipart/form-data

file: [Binary image file - JPEG, PNG, GIF, WebP - max 5MB]
caption: "Ảnh số khung" (optional)
```

**Response (200 OK):**
```json
{
  "id": 103,
  "senderId": 10,
  "senderName": "Seller A",
  "senderRole": "SELLER",
  "type": "IMAGE",
  "text": null,
  "attachmentUrl": "/uploads/inspection-chat/req9_u10_abc.png",
  "attachmentCaption": "Ảnh số khung",
  "createdAt": "2026-02-15T10:40:00"
}
```

**Error Response (423 LOCKED):**
```json
{
  "status": 423,
  "message": "Chat is locked because listing is ARCHIVED"
}
```

**Other Error Responses:**
- 404: Inspection request not found
- 403: User doesn't have access
- 400: Invalid file (not image, too large, etc.)

**Validation:**
1. File must be image type (JPEG, PNG, GIF, WebP)
2. File size max 5MB
3. Permission check
4. **Lock check:** If `listing.status == ARCHIVED` → throw 423, no file upload

---

### S-40.4: Mark as Read (Optional)

**Request:**
```
POST /api/inspection-requests/{requestId}/chat-thread/read

Authorization: Bearer JWT
Content-Type: application/json

{
  "lastReadMessageId": 103
}
```

**Response (200 OK):**
```
(no body)
```

**Permission Rules:**
- INSPECTOR: must be assigned
- SELLER: must be owner

**Important:**
- **Lock does NOT prevent mark as read**
- Always allowed regardless of listing status
- Useful for UI "unread" badge even when chat is locked

---

## 2. Implementation Details

### Lock Check Location

The lock check happens in **`InspectionChatService`** method:

```java
private void checkThreadLocked(BikeListing listing) {
    if (listing.getStatus() == BikeListingStatus.ARCHIVED) {
        throw new ResponseStatusException(HttpStatus.LOCKED, 
            "Chat is locked because listing is ARCHIVED");
    }
}
```

**Called in:**
- `sendTextMessage()` - line ~114
- `uploadImageMessage()` - line ~163

**NOT called in:**
- `getChatThread()` - allows read access even when locked
- `markThreadAsRead()` - allows marking as read when locked

### Lock Detection in Response

In **`InspectionChatThreadResponse.from()`**:

```java
public static InspectionChatThreadResponse from(InspectionChatThread thread, List<ChatMessageResponse> messages) {
    BikeListing listing = thread.getInspectionRequest().getListing();
    boolean isLocked = listing.getStatus() == BikeListingStatus.ARCHIVED;
    String lockReason = isLocked ? "LISTING_ARCHIVED" : null;

    return new InspectionChatThreadResponse(
        thread.getThreadId(),
        thread.getInspectionRequest().getRequestId(),
        listing.getListingId(),
        listing.getTitle(),
        isLocked,           // ← boolean lock status
        lockReason,         // ← string reason
        messages,
        thread.getCreatedAt(),
        thread.getUpdatedAt()
    );
}
```

**Key points:**
- Uses enum comparison: `listing.getStatus() == BikeListingStatus.ARCHIVED`
- More type-safe than string comparison
- Sets `lockedReason = "LISTING_ARCHIVED"` for client-side UI logic

### Error Response Handling

The **`GlobalExceptionHandler`** automatically formats 423 responses:

```java
@ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
    assert ex.getReason() != null;
    return ResponseEntity
        .status(ex.getStatusCode())
        .body(Map.of(
            "status", ex.getStatusCode().value(),
            "message", ex.getReason()
        ));
}
```

When service throws:
```java
throw new ResponseStatusException(HttpStatus.LOCKED, 
    "Chat is locked because listing is ARCHIVED");
```

Client receives:
```json
{
  "status": 423,
  "message": "Chat is locked because listing is ARCHIVED"
}
```

---

## 3. File Changes Summary

### Modified Files:

#### 1. **`InspectionChatThreadResponse.java`**
- ✅ Added `lockedReason` field (String)
- ✅ Updated constructor to include `lockedReason`
- ✅ Added import: `import com.example.cyclexbe.domain.enums.BikeListingStatus;`
- ✅ Updated `from()` method to use enum comparison and set `lockedReason`

#### 2. **`InspectionChatService.java`**
- ✅ Already has `checkThreadLocked()` method
- ✅ Called in `sendTextMessage()` (line ~114)
- ✅ Called in `uploadImageMessage()` (line ~163)
- ✅ Error message: "Chat is locked because listing is ARCHIVED"
- ✅ Uses enum comparison: `listing.getStatus() == BikeListingStatus.ARCHIVED`

#### 3. **`InspectionChatController.java`** (No changes needed)
- ✅ Already properly structured
- ✅ Returns ChatMessageResponse from service
- ✅ Service throws ResponseStatusException with 423, automatically handled

#### 4. **`GlobalExceptionHandler.java`** (No changes needed)
- ✅ Already handles ResponseStatusException
- ✅ Formats response as `{status, message}`

### Unchanged (Already correct):

- `InspectionChatMessage.java` - Entity structure is fine
- `InspectionChatThread.java` - Entity structure is fine
- `ChatMessageResponse.java` - Response structure is fine
- `BikeListingStatus.java` - Has ARCHIVED enum value
- `Repositories` - All required queries exist

---

## 4. Testing Scenarios

### Scenario 1: Read locked chat (should work)
```bash
GET /api/inspection-requests/9/chat-thread
Authorization: Bearer [JWT]

# Response: 200 OK
# locked=true, lockedReason="LISTING_ARCHIVED"
# messages: [all past messages visible]
```

### Scenario 2: Send message to locked chat (should fail)
```bash
POST /api/inspection-requests/9/chat-messages
Authorization: Bearer [JWT]
Content-Type: application/json

{
  "type": "TEXT",
  "text": "New message"
}

# Response: 423 LOCKED
# {
#   "status": 423,
#   "message": "Chat is locked because listing is ARCHIVED"
# }
```

### Scenario 3: Upload image to locked chat (should fail)
```bash
POST /api/inspection-requests/9/chat-messages:upload
Authorization: Bearer [JWT]
Content-Type: multipart/form-data

file: [image]
caption: "Ảnh số khung"

# Response: 423 LOCKED
# {
#   "status": 423,
#   "message": "Chat is locked because listing is ARCHIVED"
# }
```

### Scenario 4: Mark as read in locked chat (should work)
```bash
POST /api/inspection-requests/9/chat-thread/read
Authorization: Bearer [JWT]
Content-Type: application/json

{
  "lastReadMessageId": 103
}

# Response: 200 OK (no body)
# Lock does NOT prevent this
```

---

## 5. Enum Values Reference

### BikeListingStatus.java
```java
public enum BikeListingStatus {
    DRAFT,
    APPROVED,
    REJECTED,
    PENDING,
    DELETED,
    ARCHIVED  // ← Triggers chat lock
}
```

### ChatMessageType.java
```java
public enum ChatMessageType {
    TEXT,
    IMAGE
}
```

### Role.java
```java
public enum Role {
    SELLER,
    INSPECTOR,
    ADMIN,
    USER
}
```

---

## 6. Database Schema

### inspection_chat_threads
```sql
CREATE TABLE inspection_chat_threads (
    thread_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT UNIQUE NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (request_id) REFERENCES inspection_requests(request_id)
);
```

### inspection_chat_messages
```sql
CREATE TABLE inspection_chat_messages (
    message_id INT PRIMARY KEY AUTO_INCREMENT,
    thread_id INT NOT NULL,
    sender_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,  -- TEXT, IMAGE
    text TEXT,
    attachment_url VARCHAR(500),
    attachment_caption TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES inspection_chat_threads(thread_id),
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
);
```

### inspection_requests
```sql
CREATE TABLE inspection_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    listing_id INT NOT NULL,
    inspector_id INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    assigned_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (listing_id) REFERENCES bike_listings(listing_id),
    FOREIGN KEY (inspector_id) REFERENCES users(user_id)
);
```

### bike_listings
```sql
CREATE TABLE bike_listings (
    listing_id INT PRIMARY KEY AUTO_INCREMENT,
    seller_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    status ENUM('DRAFT', 'APPROVED', 'REJECTED', 'PENDING', 'DELETED', 'ARCHIVED') NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES users(user_id)
);
```

---

## 7. How Lock Status is Determined

```
Lock Status = (listing.status == BikeListingStatus.ARCHIVED)

if (locked) {
  - GET /chat-thread → 200 OK (locked=true, lockedReason="LISTING_ARCHIVED")
  - POST /chat-messages → 423 LOCKED
  - POST /chat-messages:upload → 423 LOCKED
  - POST /chat-thread/read → 200 OK (allowed)
}

if (!locked) {
  - GET /chat-thread → 200 OK (locked=false, lockedReason=null)
  - POST /chat-messages → 200 OK (message created)
  - POST /chat-messages:upload → 200 OK (image uploaded)
  - POST /chat-thread/read → 200 OK (allowed)
}
```

---

## 8. UI/Client Implementation Guidelines

### For Frontend Developers:

1. **When loading chat:**
   ```javascript
   GET /api/inspection-requests/{requestId}/chat-thread
   
   // Check response.locked
   if (response.locked) {
       showLockBanner(`Chat is locked: ${response.lockedReason}`);
       disableSendButton();
       disableUploadButton();
   } else {
       enableSendButton();
       enableUploadButton();
   }
   
   // Always display messages
   displayMessages(response.messages);
   ```

2. **When sending message:**
   ```javascript
   POST /api/inspection-requests/{requestId}/chat-messages
   
   // Handle 423 response
   .catch(error => {
       if (error.status === 423) {
           showError(`Chat is locked: ${error.response.message}`);
       }
   });
   ```

3. **When uploading image:**
   ```javascript
   POST /api/inspection-requests/{requestId}/chat-messages:upload
   
   // Handle 423 response
   .catch(error => {
       if (error.status === 423) {
           showError(`Cannot upload: Chat is locked`);
       }
   });
   ```

4. **Mark as read (always works):**
   ```javascript
   POST /api/inspection-requests/{requestId}/chat-thread/read
   
   // Lock doesn't affect this - always send
   // Useful for: unread badge, notification, etc.
   ```

---

## 9. Deployment Checklist

- ✅ `BikeListingStatus.ARCHIVED` enum exists
- ✅ `InspectionChatThreadResponse.lockedReason` field added
- ✅ `InspectionChatService.checkThreadLocked()` implemented
- ✅ `GlobalExceptionHandler` handles 423 responses
- ✅ Database schema supports `listing.status = ARCHIVED`
- ✅ API documentation updated
- ✅ Frontend ready to handle `locked` flag and 423 error

---

## 10. Example cURL Commands

### 1. Get Chat Thread
```bash
curl -X GET \
  'http://localhost:8080/api/inspection-requests/9/chat-thread' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
```

### 2. Send Text Message (Success)
```bash
curl -X POST \
  'http://localhost:8080/api/inspection-requests/9/chat-messages' \
  -H 'Authorization: Bearer [JWT]' \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "TEXT",
    "text": "Anh cần thêm ảnh số khung"
  }'
```

### 3. Send Text Message (Locked - Error)
```bash
# Returns 423 LOCKED
# {
#   "status": 423,
#   "message": "Chat is locked because listing is ARCHIVED"
# }
```

### 4. Upload Image (Success)
```bash
curl -X POST \
  'http://localhost:8080/api/inspection-requests/9/chat-messages:upload' \
  -H 'Authorization: Bearer [JWT]' \
  -F 'file=@/path/to/image.png' \
  -F 'caption=Ảnh số khung'
```

### 5. Mark as Read
```bash
curl -X POST \
  'http://localhost:8080/api/inspection-requests/9/chat-thread/read' \
  -H 'Authorization: Bearer [JWT]' \
  -H 'Content-Type: application/json' \
  -d '{
    "lastReadMessageId": 103
  }'
```

---

## 11. Troubleshooting

| Issue | Solution |
|-------|----------|
| 403 Forbidden on GET chat | User is not inspector assigned or not seller owner |
| 404 Not Found | Inspection request doesn't exist |
| 400 Bad Request | Empty text message or invalid file |
| 423 Locked on POST message | Listing is ARCHIVED, chat is locked |
| Chat shows unlocked but should be locked | Check `listing.status` in database equals `ARCHIVED` |
| lockedReason is null | Listing is not ARCHIVED (lock=false is correct) |

---

## 12. Related Documentation

- See: `INSPECTOR_CONTROLLER_COMPLETE.md` for full API documentation
- See: `BATCH_2_DOCUMENTATION_INDEX.md` for S-40 overview
- See: `SELLER_API_DOCUMENTATION.md` for seller endpoints

---

**Last Updated:** 2026-02-15  
**Version:** 1.0  
**Status:** ✅ Implementation Complete

