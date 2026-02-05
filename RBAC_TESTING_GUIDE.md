# Role-Based Access Control (RBAC) - Testing Guide

## Cấu hình hoàn thành

Đã triển khai Role-Based Access Control cho CycleX BE API:

### Các file đã thay đổi:
1. **SecurityConfig.java** - Enable `@PreAuthorize` annotation
2. **JwtFilter.java** - Xóa skip logic cho `/api/inspector/**`
3. **SecurityUtils.java** - Tạo utility class để validate resource owner
4. **InspectorController.java** - Thêm `@PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")` + validation
5. **SellerController.java** - Thêm `@PreAuthorize("hasRole('SELLER')")` + validation

---

## Cách hoạt động

### 1. Authentication Flow
```
Client Request
    ↓
JwtFilter (extract token & set user)
    ↓
SecurityContext (contains principal & authorities)
    ↓
@PreAuthorize (kiểm tra role)
    ↓
SecurityUtils.validateResourceOwner (kiểm tra pathVariable khớp userId)
    ↓
Business Logic (Service)
```

### 2. Spring Security Convention
- Role trong JWT token: `"INSPECTOR"`, `"SELLER"`, `"ADMIN"`, `"BUYER"`
- Spring Security tự thêm prefix `ROLE_` → `"ROLE_INSPECTOR"`, `"ROLE_SELLER"`
- `@PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")` → Kiểm tra nếu có role `ROLE_INSPECTOR` hoặc `ROLE_ADMIN`
- `@PreAuthorize("hasRole('SELLER')")` → Kiểm tra nếu có role `ROLE_SELLER`

---

## Test Cases

### Test 1: INSPECTOR Lock Listing (✅ Hợp lệ)

**Setup:**
- User ID: `5` (INSPECTOR)
- Token role: `"INSPECTOR"`
- Listing ID: `15`

```bash
curl -X POST "http://localhost:8080/api/inspector/5/listings/15/lock" \
  -H "Authorization: Bearer <JWT_TOKEN_INSPECTOR>" \
  -H "Content-Type: application/json"
```

**Expected Response: 200 OK**
```json
{
  "listingId": 15,
  "status": "LOCKED",
  "inspectorId": 5,
  "sellerId": 2,
  "title": "Trek FX 3 2023"
}
```

---

### Test 2: SELLER Try Lock Listing (❌ Role không đúng)

**Setup:**
- User ID: `2` (SELLER)
- Token role: `"SELLER"`
- Listing ID: `15`

```bash
curl -X POST "http://localhost:8080/api/inspector/2/listings/15/lock" \
  -H "Authorization: Bearer <JWT_TOKEN_SELLER>" \
  -H "Content-Type: application/json"
```

**Expected Response: 403 Forbidden**
```json
{
  "timestamp": "2025-02-03T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

---

### Test 3: INSPECTOR Access Other Inspector's Resource (❌ PathVariable khác)

**Setup:**
- Authenticated User ID: `5` (INSPECTOR)
- Token role: `"INSPECTOR"`
- Path Variable: `inspectorId=6` (INSPECTOR khác)

```bash
curl -X GET "http://localhost:8080/api/inspector/6/dashboard/stats" \
  -H "Authorization: Bearer <JWT_TOKEN_INSPECTOR_5>"
```

**Expected Response: 403 Forbidden**
```json
{
  "timestamp": "2025-02-03T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to access this INSPECTOR's resources"
}
```

**Lý do:** `SecurityUtils.validateResourceOwner()` kiểm tra `authenticatedUserId (5) != requestedUserId (6)`

---

### Test 4: SELLER Get Own Listings (✅ Hợp lệ)

**Setup:**
- User ID: `2` (SELLER)
- Token role: `"SELLER"`
- Listing status: `"DRAFT"`, `"PENDING"`, `"APPROVED"`

```bash
curl -X GET "http://localhost:8080/api/seller/2/listings/search?page=0&pageSize=10" \
  -H "Authorization: Bearer <JWT_TOKEN_SELLER_2>"
```

**Expected Response: 200 OK**
```json
{
  "content": [
    {
      "listingId": 10,
      "title": "Giant Talon 3 2023",
      "status": "PENDING",
      "price": 13500000
    },
    {
      "listingId": 11,
      "title": "Trek FX 3 2021",
      "status": "LOCKED"
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

---

### Test 5: SELLER Access Other Seller's Listings (❌ PathVariable khác)

**Setup:**
- Authenticated User ID: `2` (SELLER)
- Path Variable: `sellerId=3` (SELLER khác)

```bash
curl -X GET "http://localhost:8080/api/seller/3/listings/search" \
  -H "Authorization: Bearer <JWT_TOKEN_SELLER_2>"
```

**Expected Response: 403 Forbidden**
```json
{
  "message": "You don't have permission to access this SELLER's resources"
}
```

---

### Test 6: Request Without Token (❌ Không authenticated)

**Setup:**
- Không gửi JWT token

```bash
curl -X GET "http://localhost:8080/api/seller/2/listings/search"
```

**Expected Response: 401 Unauthorized**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required"
}
```

---

### Test 7: Invalid/Expired Token (❌ Token không hợp lệ)

```bash
curl -X GET "http://localhost:8080/api/seller/2/listings/search" \
  -H "Authorization: Bearer invalid_token_xyz"
```

**Expected Response: 401 Unauthorized**

---

### Test 8: ADMIN Access INSPECTOR Endpoint (✅ ADMIN can access all)

**Setup:**
- User ID: `8` (ADMIN)
- Token role: `"ADMIN"`

```bash
curl -X POST "http://localhost:8080/api/inspector/5/listings/15/lock" \
  -H "Authorization: Bearer <JWT_TOKEN_ADMIN>"
```

**Expected Response: 200 OK** (vì ADMIN có `hasAnyRole('INSPECTOR', 'ADMIN')`)

---

## Postman Testing

### Import Environment Variables

```json
{
  "id": "cyclexbe-env",
  "name": "CycleX BE",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080",
      "enabled": true
    },
    {
      "key": "inspector_5_token",
      "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "enabled": true
    },
    {
      "key": "seller_2_token",
      "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "enabled": true
    },
    {
      "key": "admin_8_token",
      "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "enabled": true
    },
    {
      "key": "inspector_id",
      "value": "5",
      "enabled": true
    },
    {
      "key": "seller_id",
      "value": "2",
      "enabled": true
    }
  ]
}
```

### Request Headers (All Authenticated Requests)
```
Authorization: Bearer {{inspector_5_token}}
Content-Type: application/json
```

---

## Keycloak/OAuth2 Integration (Future)

Nếu muốn integrate với Keycloak, chỉ cần:

1. Thay đổi `JwtProvider` để validate with Keycloak public key
2. Role mapping từ Keycloak groups
3. `@PreAuthorize` không cần thay đổi

---

## Common Errors & Solutions

| Error | Nguyên nhân | Giải pháp |
|-------|-----------|----------|
| `403 Forbidden` | Role không đúng | Kiểm tra JWT token role |
| `403 Forbidden` | PathVariable khác userId | Gửi request với đúng user ID |
| `401 Unauthorized` | Không gửi token | Thêm `Authorization: Bearer <token>` header |
| `401 Unauthorized` | Token expired | Refresh token |
| `401 Unauthorized` | Token invalid | Check token format & signature |

---

## Các điểm lưu ý

1. **PathVariable không bắt buộc khớp**: Bạn có thể check user khác nếu có permission, nhưng hiện tại được protect bởi `SecurityUtils.validateResourceOwner()`

2. **Admin Access**: `@PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")` cho phép ADMIN access INSPECTOR endpoints

3. **No Override**: `@PreAuthorize` tại method level sẽ **override** class level nếu bạn muốn permission khác

4. **Custom Role Checker**: Nếu cần logic phức tạp, viết `@PreAuthorize("@customSecurityService.canAccessListing(#listingId)")` với `@Component` class

---

## Deployment Checklist

- [ ] Environment variable `JWT_SECRET` được set (production)
- [ ] SSL/TLS enable trên production
- [ ] CORS config đúng (nếu frontend & backend khác domain)
- [ ] Token TTL/Expiration được set hợp lý
- [ ] Audit logging enable cho authorization failures
- [ ] Rate limiting enable để prevent brute force
