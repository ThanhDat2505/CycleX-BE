package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.InspectionChatMessageResponse;
import com.example.cyclexbe.dto.InspectionChatThreadResponse;
import com.example.cyclexbe.dto.SendInspectionChatTextRequest;
import com.example.cyclexbe.entity.*;
import com.example.cyclexbe.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InspectionChatService {

    private final InspectionRequestRepository requestRepo;
    private final InspectionReportRepository reportRepo;
    private final InspectionMediaRepository mediaRepo;

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public InspectionChatService(InspectionRequestRepository requestRepo,
                                 InspectionReportRepository reportRepo,
                                 InspectionMediaRepository mediaRepo) {
        this.requestRepo = requestRepo;
        this.reportRepo = reportRepo;
        this.mediaRepo = mediaRepo;
    }

    public InspectionRequest getRequestOrThrow(Integer requestId) {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("InspectionRequest not found"));
    }

    private void requireMember(InspectionRequest req, Integer userId) {
        Integer sellerId = req.getSeller().getUserId();
        Integer inspectorId = req.getInspector() == null ? null : req.getInspector().getUserId();

        boolean ok = userId.equals(sellerId) || (inspectorId != null && userId.equals(inspectorId));
        if (!ok) throw new SecurityException("Not allowed");
    }

    private String roleOf(InspectionRequest req, Integer userId) {
        if (userId.equals(req.getSeller().getUserId())) return "SELLER";
        if (req.getInspector() != null && userId.equals(req.getInspector().getUserId())) return "INSPECTOR";
        return "UNKNOWN";
    }

    private void requireNotArchived(InspectionRequest req) {
        String rs = req.getStatus() == null ? "" : req.getStatus().toUpperCase();
        if (rs.equals("COMPLETED") || rs.equals("CANCELLED") || rs.equals("ARCHIVED")) {
            throw new IllegalStateException("Chat archived (request status=" + req.getStatus() + ")");
        }

        // thêm lock theo report nếu muốn
        reportRepo.findByListing_ListingId(req.getListing().getListingId()).ifPresent(r -> {
            String st = r.getStatus() == null ? "" : r.getStatus().toUpperCase();
            if (st.equals("COMPLETED") || st.equals("APPROVED") || st.equals("REJECTED")) {
                throw new IllegalStateException("Chat archived (report status=" + r.getStatus() + ")");
            }
        });
    }

    private String formatLine(LocalDateTime time, String role, Integer userId, String msg) {
        String safe = msg == null ? "" : msg.replace("\n", " ").trim();
        return "[" + time.format(F) + "] (" + role + "#" + userId + "): " + safe;
    }

    private InspectionReport ensureReport(InspectionRequest req) {
        Integer listingId = req.getListing().getListingId();
        return reportRepo.findByListing_ListingId(listingId).orElseGet(() -> {
            if (req.getInspector() == null) {
                throw new IllegalStateException("Inspector not assigned yet");
            }
            InspectionReport r = new InspectionReport();
            r.setListing(req.getListing());
            r.setInspector(req.getInspector());
            r.setStatus("pending");
            r.setCreatedAt(LocalDateTime.now());
            return reportRepo.save(r);
        });
    }

    @Transactional
    public void sendText(Integer requestId, Integer currentUserId, String message) {
        InspectionRequest req = getRequestOrThrow(requestId);
        requireMember(req, currentUserId);
        requireNotArchived(req);

        String role = roleOf(req, currentUserId);
        LocalDateTime now = LocalDateTime.now();
        String line = formatLine(now, role, currentUserId, message);

        String old = req.getNote();
        if (old == null || old.isBlank()) req.setNote(line);
        else req.setNote(old + "\n" + line);

        requestRepo.save(req);
    }

    @Transactional
    public void sendImage(Integer requestId, Integer currentUserId, String caption, String imageUrl) {
        InspectionRequest req = getRequestOrThrow(requestId);
        requireMember(req, currentUserId);
        requireNotArchived(req);

        // log vào note để biết ai gửi (vì InspectionMedia không có uploaded_by)
        String role = roleOf(req, currentUserId);
        String msg = (caption == null || caption.isBlank()) ? "[IMAGE]" : "[IMAGE] " + caption;
        LocalDateTime now = LocalDateTime.now();
        String line = formatLine(now, role, currentUserId, msg);

        String old = req.getNote();
        if (old == null || old.isBlank()) req.setNote(line);
        else req.setNote(old + "\n" + line);
        requestRepo.save(req);

        // lưu ảnh vào InspectionMedia category=CHAT
        InspectionReport report = ensureReport(req);

        InspectionMedia media = new InspectionMedia();
        media.setInspection(report);
        media.setMediaType("IMAGE");
        media.setCategory("CHAT");
        media.setMediaUrl(imageUrl);
        media.setUploadedAt(now);

        mediaRepo.save(media);
    }

    @Transactional
    public List<InspectionChatMessageResponse> getThread(Integer requestId, Integer currentUserId) {
        InspectionRequest req = getRequestOrThrow(requestId);
        requireMember(req, currentUserId);

        List<InspectionChatMessageResponse> out = new ArrayList<>();

        // TEXT từ note
        String note = req.getNote();
        if (note != null && !note.isBlank()) {
            String[] lines = note.split("\\R");
            for (String line : lines) {
                InspectionChatMessageResponse r = new InspectionChatMessageResponse();
                r.type = "TEXT";
                r.content = line;
                // đơn giản: giữ raw line, FE render luôn
                out.add(r);
            }
        }

        // IMAGE từ InspectionMedia(category CHAT)
        reportRepo.findByListing_ListingId(req.getListing().getListingId()).ifPresent(report -> {
            List<InspectionMedia> images = mediaRepo
                    .findByInspection_InspectionIdAndCategoryOrderByUploadedAtAsc(report.getInspectionId(), "CHAT");
            for (InspectionMedia im : images) {
                InspectionChatMessageResponse r = new InspectionChatMessageResponse();
                r.type = "IMAGE";
                r.mediaUrl = im.getMediaUrl();
                r.createdAt = im.getUploadedAt();
                out.add(r);
            }
        });

        // sort nếu muốn (note TEXT không parse time nên chỉ sort ảnh theo time thôi)
        return out;
    }
}
