package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.*;
import com.example.cyclexbe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InspectionResponseService {

    private final InspectionRequestRepository inspectionRequestRepository;
    private final InspectionResponseRepository inspectionResponseRepository;
    private final InspectionResponseFileRepository inspectionResponseFileRepository;
    private final InspectionRequirementRepository inspectionRequirementRepository;
    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;

    private static final int MAX_FILES = 10;
    private static final int MAX_FILE_SIZE_MB = 10;
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "application/pdf");

    public InspectionResponseService(
            InspectionRequestRepository inspectionRequestRepository,
            InspectionResponseRepository inspectionResponseRepository,
            InspectionResponseFileRepository inspectionResponseFileRepository,
            InspectionRequirementRepository inspectionRequirementRepository,
            BikeListingRepository bikeListingRepository,
            UserRepository userRepository
    ) {
        this.inspectionRequestRepository = inspectionRequestRepository;
        this.inspectionResponseRepository = inspectionResponseRepository;
        this.inspectionResponseFileRepository = inspectionResponseFileRepository;
        this.inspectionRequirementRepository = inspectionRequirementRepository;
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
    }

    // ===== helper =====

    private void assertOwnerAndStatus(BikeListing listing, Integer sellerId) {
        if (!listing.getSeller().getUserId().equals(sellerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this listing");
        }

        // Need More Info / Waiting Seller Response (hoặc tương đương)
        if (listing.getStatus() != BikeListingStatus.NEED_MORE_INFO
            /* || listing.getStatus() == BikeListingStatus.WAITING_SELLER_RESPONSE */) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Listing is not eligible for S-42");
        }
    }

    private boolean isLocked(InspectionResponse response) {
        return response != null && "SUBMITTED".equalsIgnoreCase(response.getStatus());
    }

    // ===== S-42.1: load screen =====
    @Transactional(readOnly = true)
    public InspectionResponseLoadResponse loadScreen(Integer listingId, Integer sellerId) {
        BikeListing listing = bikeListingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        assertOwnerAndStatus(listing, sellerId);

        InspectionRequest req = inspectionRequestRepository.findByListing_ListingId(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        InspectionResponse response = inspectionResponseRepository
                .findByInspectionRequest_RequestId(req.getRequestId())
                .orElse(null);

        List<InspectionRequirementResponse> requirementResponses =
                inspectionRequirementRepository
                        .findByInspectionRequest_RequestIdAndResolvedFalse(req.getRequestId())
                        .stream().map(InspectionRequirementResponse::from)
                        .collect(Collectors.toList());

        List<InspectionResponseFileResponse> fileResponses = Collections.emptyList();
        if (response != null) {
            fileResponses = inspectionResponseFileRepository
                    .findByInspectionResponse_InspectionRequest_RequestIdAndStatus(req.getRequestId(), "DRAFT")
                    .stream().map(InspectionResponseFileResponse::from)
                    .collect(Collectors.toList());
        }

        return InspectionResponseLoadResponse.from(listing, req, response, requirementResponses, fileResponses);
    }

    // ===== S-42.2: upload file draft (GẮN VỚI REQUIREMENT) =====
    // NOTE: để đúng BR-F04, endpoint nên có requirementId:
    // POST /inspection-requests/{inspectionRequestId}/response/requirements/{requirementId}/files
    @Transactional
    public InspectionResponseFileResponse uploadFile(Integer inspectionRequestId,
                                                     Integer requirementId,
                                                     Integer sellerId,
                                                     MultipartFile file) {

        InspectionRequest req = inspectionRequestRepository.findById(inspectionRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        BikeListing listing = req.getListing();
        assertOwnerAndStatus(listing, sellerId);

        InspectionResponse response = inspectionResponseRepository
                .findByInspectionRequest_RequestId(inspectionRequestId)
                .orElseGet(() -> inspectionResponseRepository.save(new InspectionResponse(req)));

        if (isLocked(response)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RESPONSE_LOCKED");
        }

        // requirement phải thuộc request và đang unresolved (đúng màn S-42)
        InspectionRequirement requirement = inspectionRequirementRepository
                .findById(requirementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requirement not found"));

        if (!requirement.getInspectionRequest().getRequestId().equals(inspectionRequestId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requirement not in this inspection request");
        }
        if (requirement.isResolved()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requirement already resolved");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type not allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE_MB * 1024L * 1024L) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File size exceeds limit");
        }

        long draftCount = inspectionResponseFileRepository
                .countByInspectionResponse_InspectionRequest_RequestIdAndStatus(inspectionRequestId, "DRAFT");
        if (draftCount >= MAX_FILES) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MAX_FILES_EXCEEDED");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        InspectionResponseFile rf = new InspectionResponseFile();
        rf.setInspectionResponse(response);
        rf.setSeller(seller);
        rf.setRequirement(requirement);              // ✅ gắn đúng requirement
        rf.setOriginalFileName(file.getOriginalFilename());
        rf.setContentType(contentType);
        rf.setSizeBytes(file.getSize());
        rf.setStatus("DRAFT");
        rf.setFileUrl("TEMP");

        InspectionResponseFile saved = inspectionResponseFileRepository.save(rf);

        saved.setFileUrl("/uploads/inspection/" + saved.getFileId());
        saved = inspectionResponseFileRepository.save(saved);

        return InspectionResponseFileResponse.from(saved);
    }

    // ===== S-42.3: delete draft file =====
    @Transactional
    public void deleteFile(Integer inspectionRequestId, Integer fileId, Integer sellerId) {

        InspectionResponseFile file = inspectionResponseFileRepository
                .findByFileIdAndInspectionResponse_InspectionRequest_RequestId(fileId, inspectionRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        if (!file.getSeller().getUserId().equals(sellerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this file");
        }

        InspectionResponse response = file.getInspectionResponse();
        if (isLocked(response)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RESPONSE_LOCKED");
        }

        BikeListing listing = response.getInspectionRequest().getListing();
        assertOwnerAndStatus(listing, sellerId);

        if (!"DRAFT".equalsIgnoreCase(file.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "File cannot be deleted");
        }

        inspectionResponseFileRepository.delete(file);
    }

    // ===== S-42.4: submit answers =====
    @Transactional
    public SubmitInspectionResponseResult submit(Integer inspectionRequestId,
                                                 Integer sellerId,
                                                 SubmitInspectionResponseRequest body) {

        InspectionRequest inspReq = inspectionRequestRepository.findById(inspectionRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        BikeListing listing = inspReq.getListing();
        assertOwnerAndStatus(listing, sellerId);

        InspectionResponse response = inspectionResponseRepository
                .findByInspectionRequest_RequestId(inspectionRequestId)
                .orElseGet(() -> inspectionResponseRepository.save(new InspectionResponse(inspReq)));

        if (isLocked(response)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "RESPONSE_LOCKED");
        }

        // unresolved requirements của request (đúng BR-F01)
        List<InspectionRequirement> unresolved = inspectionRequirementRepository
                .findByInspectionRequest_RequestIdAndResolvedFalse(inspectionRequestId);

        Map<Integer, InspectionRequirement> unresolvedMap = unresolved.stream()
                .collect(Collectors.toMap(InspectionRequirement::getRequirementId, r -> r));

        // ===== F02: chỉ được trả lời đúng các requirement đã đưa ra =====
        // Nếu FE gửi requirementId lạ -> 400
        for (SubmitInspectionResponseRequest.AnswerItem ans : body.getAnswers()) {
            if (ans.getRequirementId() == null || !unresolvedMap.containsKey(ans.getRequirementId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer contains invalid requirementId");
            }
        }

        // build map answers by requirementId
        Map<Integer, String> answers = new HashMap<>();
        for (SubmitInspectionResponseRequest.AnswerItem ans : body.getAnswers()) {
            answers.put(ans.getRequirementId(), ans.getText());
        }

        // ===== F03: validate required text per requirement =====
        for (InspectionRequirement req : unresolved) {
            if (req.isRequiredText()) {
                String text = answers.get(req.getRequirementId());
                if (text == null || text.trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Missing required text for requirementId=" + req.getRequirementId());
                }
            }
        }

        // ===== F07: validate required files per requirement =====
        for (InspectionRequirement req : unresolved) {
            if (req.isRequiredFiles()) {
                long cnt = inspectionResponseFileRepository
                        .countByInspectionResponse_InspectionRequest_RequestIdAndStatusAndRequirement_RequirementId(
                                inspectionRequestId, "DRAFT", req.getRequirementId());
                if (cnt == 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Missing required files for requirementId=" + req.getRequirementId());
                }
            }
        }

        // max files safety
        long draftCountNow = inspectionResponseFileRepository
                .countByInspectionResponse_InspectionRequest_RequestIdAndStatus(inspectionRequestId, "DRAFT");
        if (draftCountNow > MAX_FILES) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MAX_FILES_EXCEEDED");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        // Lưu message tổng hợp (vì entity có message 1 field)
        // Bạn có thể format từ answers để giữ log rõ ràng.
        String mergedMessage = body.getAnswers().stream()
                .map(a -> "Requirement#" + a.getRequirementId() + ": " + (a.getText() == null ? "" : a.getText()))
                .collect(Collectors.joining("\n"));

        response.setSeller(seller);
        response.setMessage(mergedMessage);
        response.setStatus("SUBMITTED");
        response.setSubmittedAt(LocalDateTime.now());
        InspectionResponse savedResponse = inspectionResponseRepository.save(response);

        // mark unresolved -> resolved (đúng BR-F01)
        for (InspectionRequirement r : unresolved) {
            r.setResolved(true);
        }
        inspectionRequirementRepository.saveAll(unresolved);

        // mark ALL DRAFT files -> SUBMITTED (các file draft thuộc response)
        List<InspectionResponseFile> draftFiles = inspectionResponseFileRepository
                .findByInspectionResponse_InspectionRequest_RequestIdAndStatus(inspectionRequestId, "DRAFT");
        for (InspectionResponseFile f : draftFiles) {
            f.setStatus("SUBMITTED");
        }
        inspectionResponseFileRepository.saveAll(draftFiles);

        // update listing status (BR-F09)
        listing.setStatus(BikeListingStatus.WAITING_INSPECTOR_REVIEW);
        bikeListingRepository.save(listing);

        return new SubmitInspectionResponseResult(
                true,
                savedResponse.getResponseId(),
                listing.getListingId(),
                BikeListingStatus.WAITING_INSPECTOR_REVIEW.toString()
        );
    }
}