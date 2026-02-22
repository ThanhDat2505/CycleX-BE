package com.example.cyclexbe.controller;

import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.dto.ChatMessageResponse;
import com.example.cyclexbe.dto.InspectionChatThreadResponse;
import com.example.cyclexbe.dto.MarkChatReadRequest;
import com.example.cyclexbe.dto.SendChatMessageRequest;
import com.example.cyclexbe.service.InspectionChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * InspectionChatController - S-40: Inspection Chat Thread
 *
 * Endpoints:
 * GET /api/v1/inspection-requests/{requestId}/chat-thread
 *   - Load thread + all messages
 *
 * POST /api/v1/inspection-requests/{requestId}/chat-messages
 *   - Send TEXT message
 *
 * POST /api/v1/inspection-requests/{requestId}/chat-messages:upload
 *   - Upload image + create IMAGE message
 *
 * POST /api/v1/inspection-requests/{requestId}/chat-thread/read
 *   - Mark as read (optional)
 */
@RestController
@RequestMapping("/api/inspection-requests")
public class InspectionChatController {

    private final InspectionChatService inspectionChatService;

    public InspectionChatController(InspectionChatService inspectionChatService) {
        this.inspectionChatService = inspectionChatService;
    }

    /**
     * S-40.1: Load Chat Thread + Messages
     *
     * GET /api/inspection-requests/{requestId}/chat-thread
     *
     * Permission:
     * - INSPECTOR: must be assigned to this inspection request
     * - SELLER: must be owner of the listing
     *
     * Response:
     * {
     *   "threadId": 1,
     *   "requestId": 1,
     *   "listingId": 5,
     *   "listingTitle": "Yamaha YZF-R1 2023",
     *   "locked": false,
     *   "messages": [
     *     {
     *       "id": 1,
     *       "senderId": 10,
     *       "senderName": "Inspector Name",
     *       "senderRole": "INSPECTOR",
     *       "type": "TEXT",
     *       "text": "Hello, I need more info about this bike",
     *       "attachmentUrl": null,
     *       "attachmentCaption": null,
     *       "createdAt": "2026-02-13T10:30:00"
     *     }
     *   ],
     *   "createdAt": "2026-02-13T10:00:00",
     *   "updatedAt": "2026-02-13T10:30:00"
     * }
     */
    @GetMapping("/{requestId}/chat-thread")
    public ResponseEntity<InspectionChatThreadResponse> getChatThread(
            @PathVariable Integer requestId,
            Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        Role userRole = getRoleFromAuth(authentication);

        InspectionChatThreadResponse response = inspectionChatService.getChatThread(requestId, userId, userRole);
        return ResponseEntity.ok(response);

    }

    /**
     * S-40.2: Send TEXT Message
     *
     * POST /api/v1/inspection-requests/{requestId}/chat-messages
     *
     * Request body:
     * {
     *   "type": "TEXT",
     *   "text": "Message content here"
     * }
     *
     * Permission:
     * - INSPECTOR or SELLER (same as getChatThread)
     *
     * Error responses:
     * - 404: Inspection request not found
     * - 403: User doesn't have access to this request
     * - 423: Chat thread is locked (listing archived)
     * - 400: Invalid input (empty text)
     *
     * Response: ChatMessageResponse
     */
    @PostMapping("/{requestId}/chat-messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Integer requestId,
            @Valid @RequestBody SendChatMessageRequest req,
            Authentication authentication) {

        Integer senderId = getUserIdFromAuth(authentication);
        Role senderRole = getRoleFromAuth(authentication);

        // Validate type
        if (!req.type.equalsIgnoreCase("TEXT")) {
            return ResponseEntity.badRequest().build();
        }

        ChatMessageResponse response = inspectionChatService.sendTextMessage(requestId, req.text, senderId, senderRole);
        return ResponseEntity.ok(response);
    }

    /**
     * S-40.3: Upload Image + Create IMAGE Message
     *
     * POST /api/v1/inspection-requests/{requestId}/chat-messages:upload
     *
     * Multipart form-data:
     * - file: Binary image file (JPEG, PNG, GIF, WebP) - max 5MB
     * - caption: Optional caption text
     *
     * Validation:
     * - File must be image type
     * - File size max 5MB
     * - Thread must not be locked
     * - Permission check (INSPECTOR or SELLER)
     *
     * Error responses:
     * - 404: Inspection request not found
     * - 403: User doesn't have access to this request
     * - 423: Chat thread is locked (listing archived)
     * - 400: Invalid file (not image, too large, etc.)
     *
     * Response: ChatMessageResponse with type="IMAGE"
     */
    @PostMapping("/{requestId}/chat-messages:upload")
    public ResponseEntity<ChatMessageResponse> uploadImageMessage(
            @PathVariable Integer requestId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption,
            Authentication authentication) throws IOException {

        Integer senderId = getUserIdFromAuth(authentication);
        Role senderRole = getRoleFromAuth(authentication);

        ChatMessageResponse response = inspectionChatService.uploadImageMessage(requestId, file, caption, senderId, senderRole);
        return ResponseEntity.ok(response);
    }

    /**
     * S-40.4 (Optional): Mark Chat Thread as Read
     *
     * POST /api/v1/inspection-requests/{requestId}/chat-thread/read
     *
     * Request body:
     * {
     *   "lastReadMessageId": 42
     * }
     *
     * Purpose: Track which messages have been read by the user
     * (Useful for unread badge count on UI)
     *
     * Error responses:
     * - 404: Inspection request or message not found
     * - 403: User doesn't have access to this request
     *
     * Response: 200 OK
     */
    @PostMapping("/{requestId}/chat-thread/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer requestId,
            @Valid @RequestBody MarkChatReadRequest req,
            Authentication authentication) {

        Integer userId = getUserIdFromAuth(authentication);
        Role userRole = getRoleFromAuth(authentication);

        inspectionChatService.markThreadAsRead(requestId, req.lastReadMessageId, userId, userRole);
        return ResponseEntity.ok().build();
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Extract userId from JWT token in Authentication
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        // JWT subject is stored as "userId:role"
        // Extract userId part
        String subject = authentication.getName();
        if (subject != null && subject.contains(":")) {
            try {
                return Integer.parseInt(subject.split(":")[0]);
            } catch (Exception e) {
                return null;
            }
        }
        try {
            return Integer.parseInt(subject);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract role from JWT token in Authentication
     */
    private Role getRoleFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        // Extract role from granted authorities
        for (var authority : authentication.getAuthorities()) {
            String roleName = authority.getAuthority();
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring("ROLE_".length());
            }
            try {
                return Role.valueOf(roleName);
            } catch (IllegalArgumentException e) {
                // Invalid role, continue
            }
        }

        return null;
    }
}

