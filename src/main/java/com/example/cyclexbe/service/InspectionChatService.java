package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.ChatMessageType;
import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.dto.ChatMessageResponse;
import com.example.cyclexbe.dto.InspectionChatThreadResponse;
import com.example.cyclexbe.entity.*;
import com.example.cyclexbe.repository.*;
import com.example.cyclexbe.util.FileUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InspectionChatService - Quản lý chat thread giữa inspector và seller
 *
 * S-40: Inspection Chat Thread
 * - Load chat thread + messages
 * - Gửi message TEXT
 * - Upload ảnh + tạo message IMAGE
 * - Mark read (optional)
 * - Permission check + lock check
 */
@Service
public class InspectionChatService {

    private final InspectionRequestRepository inspectionRequestRepository;
    private final InspectionChatThreadRepository chatThreadRepository;
    private final InspectionChatMessageRepository chatMessageRepository;
    private final BikeListingRepository bikeListingRepository;
    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;

    public InspectionChatService(
            InspectionRequestRepository inspectionRequestRepository,
            InspectionChatThreadRepository chatThreadRepository,
            InspectionChatMessageRepository chatMessageRepository,
            BikeListingRepository bikeListingRepository,
            UserRepository userRepository,
            FileUploadUtil fileUploadUtil
    ) {
        this.inspectionRequestRepository = inspectionRequestRepository;
        this.chatThreadRepository = chatThreadRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.bikeListingRepository = bikeListingRepository;
        this.userRepository = userRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

    /**
     * Load chat thread + messages for a given inspection request
     *
     * Permission check:
     * - INSPECTOR: must be assigned to this inspection request
     * - SELLER: must be owner of the listing
     *
     * If thread doesn't exist, auto-create it
     */
    @Transactional
    public InspectionChatThreadResponse getChatThread(Integer requestId, Integer currentUserId, Role currentRole) {
        // Fetch inspection request
        InspectionRequest request = inspectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        // Permission check
        checkAccessPermission(request, currentUserId, currentRole);

        // Fetch or create chat thread
        InspectionChatThread thread = chatThreadRepository.findByInspectionRequest_RequestId(requestId)
                .orElseGet(() -> {
                    InspectionChatThread newThread = new InspectionChatThread(request);
                    return chatThreadRepository.save(newThread);
                });

        // Fetch all messages
        List<InspectionChatMessage> messages = chatMessageRepository.findByChatThread_ThreadIdOrderByCreatedAtAsc(thread.getThreadId());
        List<ChatMessageResponse> messageResponses = messages.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        return InspectionChatThreadResponse.from(thread, messageResponses);
    }

    /**
     * Send a TEXT message to the chat thread
     *
     * Validation:
     * - text cannot be empty
     * - thread must not be locked (listing status != ARCHIVED)
     * - permission check
     */
    @Transactional
    public ChatMessageResponse sendTextMessage(Integer requestId, String text, Integer senderId, Role senderRole) {
        // Validate text
        if (text == null || text.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be empty");
        }

        // Fetch inspection request
        InspectionRequest request = inspectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        // Permission check
        checkAccessPermission(request, senderId, senderRole);

        // Check if thread is locked
        checkThreadLocked(request.getListing());

        // Fetch or create chat thread
        InspectionChatThread thread = chatThreadRepository.findByInspectionRequest_RequestId(requestId)
                .orElseGet(() -> {
                    InspectionChatThread newThread = new InspectionChatThread(request);
                    return chatThreadRepository.save(newThread);
                });

        // Fetch sender user
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        // Create message
        InspectionChatMessage message = new InspectionChatMessage(thread, sender, ChatMessageType.TEXT, text);
        InspectionChatMessage saved = chatMessageRepository.save(message);

        return ChatMessageResponse.from(saved);
    }

    /**
     * Upload image and create IMAGE message
     *
     * Validation:
     * - file must be image (JPEG, PNG, GIF, WebP)
     * - file size max 5MB
     * - thread must not be locked
     * - permission check
     */
    @Transactional
    public ChatMessageResponse uploadImageMessage(Integer requestId,
                                                  MultipartFile file,
                                                  String caption,
                                                  Integer senderId,
                                                  Role senderRole) throws IOException {

        // 1) Validate file
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty");
        }

        // 2) Fetch inspection request
        InspectionRequest request = inspectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        // 3) Permission check
        checkAccessPermission(request, senderId, senderRole);

        // 4) Lock check (ARCHIVED)
        checkThreadLocked(request.getListing());

        // 5) Upload file SAU KHI đã pass permission + lock
        String fileUrl;
        try {
            fileUrl = fileUploadUtil.uploadImage(file);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        // 6) Fetch/create thread
        InspectionChatThread thread = chatThreadRepository.findByInspectionRequest_RequestId(requestId)
                .orElseGet(() -> chatThreadRepository.save(new InspectionChatThread(request)));

        // 7) Fetch sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        // 8) Create IMAGE message
        InspectionChatMessage message = new InspectionChatMessage(thread, sender, ChatMessageType.IMAGE, null);
        message.setAttachmentUrl(fileUrl);
        message.setAttachmentCaption(caption);

        InspectionChatMessage saved = chatMessageRepository.save(message);
        return ChatMessageResponse.from(saved);
    }

    /**
     * Mark last read message for a user
     * Useful for tracking unread status (optional feature)
     */
    @Transactional
    public void markThreadAsRead(Integer requestId, Integer lastReadMessageId, Integer userId, Role userRole) {
        // Fetch inspection request
        InspectionRequest request = inspectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection request not found"));

        // Permission check
        checkAccessPermission(request, userId, userRole);

        // Fetch chat thread
        InspectionChatThread thread = chatThreadRepository.findByInspectionRequest_RequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat thread not found"));

        // Verify message exists in this thread
        if (lastReadMessageId != null) {
            chatMessageRepository.findById(lastReadMessageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        }

        // Update last_read_at timestamp (nếu thêm vào sau)
        // Hiện tại chỉ validation, không lưu
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Check if user has permission to access this inspection request
     *
     * - INSPECTOR: must be the assigned inspector
     * - SELLER: must be the owner of the listing
     * - ADMIN: always allowed (optional)
     */
    private void checkAccessPermission(InspectionRequest request, Integer userId, Role userRole) {
        if (userRole == Role.INSPECTOR) {
            if (!request.getInspector().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not assigned to this inspection request");
            }
        } else if (userRole == Role.SELLER) {
            BikeListing listing = request.getListing();
            if (!listing.getSeller().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the seller of this listing");
            }
        } else if (userRole != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role for chat access");
        }
    }

    /**
     * Check if chat thread is locked (listing status = ARCHIVED)
     * Throw 423 Locked if locked
     */
    private void checkThreadLocked(BikeListing listing) {
        if (listing.getStatus() == BikeListingStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "Chat is locked because listing is ARCHIVED");
        }
    }
}

