package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.SendInspectionChatTextRequest;
import com.example.cyclexbe.dto.InspectionChatMessageResponse;
import com.example.cyclexbe.security.AuthContext;
import com.example.cyclexbe.service.InspectionChatService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/inspection-chat")
public class InspectionChatController {

    private final InspectionChatService chatService;

    public InspectionChatController(InspectionChatService chatService) {
        this.chatService = chatService;
    }

    private Integer uid() { return AuthContext.getUserId(); } // bạn chỉnh theo JWT principal

    @PostMapping("/text")
    public ResponseEntity<Void> sendText(@Valid @RequestBody SendInspectionChatTextRequest req) {
        chatService.sendText(req.requestId, uid(), req.message);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<List<InspectionChatMessageResponse>> thread(@PathVariable Integer requestId) {
        return ResponseEntity.ok(chatService.getThread(requestId, uid()));
    }

}
