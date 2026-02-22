package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.InspectionResponseFileResponse;
import com.example.cyclexbe.dto.InspectionResponseLoadResponse;
import com.example.cyclexbe.dto.SubmitInspectionResponseRequest;
import com.example.cyclexbe.dto.SubmitInspectionResponseResult;
import com.example.cyclexbe.service.InspectionResponseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * InspectionResponseController - S-42: Seller Inspection Response Screen
 *
 * Endpoints:
 * GET    /api/seller/listings/{listingId}/inspection-response
 * POST   /api/seller/inspection-requests/{inspectionRequestId}/response/requirements/{requirementId}/files
 * DELETE /api/seller/inspection-requests/{inspectionRequestId}/response/files/{fileId}
 * POST   /api/seller/inspection-requests/{inspectionRequestId}/response/submit
 */
@RestController
@RequestMapping("/api/seller")
public class InspectionResponseController {

    private final InspectionResponseService inspectionResponseService;

    public InspectionResponseController(InspectionResponseService inspectionResponseService) {
        this.inspectionResponseService = inspectionResponseService;
    }

    // S-42.1 Load screen
    @GetMapping("/listings/{listingId}/inspection-response")
    public ResponseEntity<InspectionResponseLoadResponse> loadScreen(
            @PathVariable Integer listingId,
            Authentication authentication) {

        Integer sellerId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(inspectionResponseService.loadScreen(listingId, sellerId));
    }

    // S-42.2 Upload draft file (gắn với requirementId để đúng BR-F04/F07)
    @PostMapping("/inspection-requests/{inspectionRequestId}/response/requirements/{requirementId}/files")
    public ResponseEntity<InspectionResponseFileResponse> uploadFile(
            @PathVariable Integer inspectionRequestId,
            @PathVariable Integer requirementId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        Integer sellerId = getUserIdFromAuth(authentication);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        InspectionResponseFileResponse resp =
                inspectionResponseService.uploadFile(inspectionRequestId, requirementId, sellerId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // S-42.3 Delete draft file
    @DeleteMapping("/inspection-requests/{inspectionRequestId}/response/files/{fileId}")
    public ResponseEntity<DeleteFileResponse> deleteFile(
            @PathVariable Integer inspectionRequestId,
            @PathVariable Integer fileId,
            Authentication authentication) {

        Integer sellerId = getUserIdFromAuth(authentication);
        inspectionResponseService.deleteFile(inspectionRequestId, fileId, sellerId);
        return ResponseEntity.ok(new DeleteFileResponse(true));
    }

    // S-42.4 Submit response (answers[] để đúng BR-F02/F03)
    @PostMapping("/inspection-requests/{inspectionRequestId}/response/submit")
    public ResponseEntity<SubmitInspectionResponseResult> submitResponse(
            @PathVariable Integer inspectionRequestId,
            @Valid @RequestBody SubmitInspectionResponseRequest request,
            Authentication authentication) {

        Integer sellerId = getUserIdFromAuth(authentication);
        SubmitInspectionResponseResult resp =
                inspectionResponseService.submit(inspectionRequestId, sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /**
     * Lấy userId từ Authentication.
     *
     * QUAN TRỌNG: đoạn này phụ thuộc cách bạn set principal trong JwtFilter.
     * - Nếu principal là userId (String) => parseInt OK.
     * - Nếu principal là UserDetails/custom => bạn phải lấy đúng field userId.
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Object principal = authentication.getPrincipal();

        // Case 1: principal is userId string
        if (principal instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid principal userId");
            }
        }

        // Case 2: principal.toString() is userId (fallback)
        try {
            return Integer.parseInt(principal.toString());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication principal");
        }
    }

    // Simple response DTO for delete
    public static class DeleteFileResponse {
        private boolean deleted;

        public DeleteFileResponse() {}
        public DeleteFileResponse(boolean deleted) { this.deleted = deleted; }

        public boolean isDeleted() { return deleted; }
        public void setDeleted(boolean deleted) { this.deleted = deleted; }
    }
}