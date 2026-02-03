package com.no_plan.library_api.controller;

import com.no_plan.library_api.dto.AiRecommendRequest;
import com.no_plan.library_api.dto.AiRecommendResponse;
import com.no_plan.library_api.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiSearchController {

    private final AiService aiService;

    @PostMapping("/recommend")
    public ResponseEntity<AiRecommendResponse> recommendBooks(@RequestBody AiRecommendRequest request) {
        AiRecommendResponse response = aiService.getRecommendations(request.getQuery());
        return ResponseEntity.ok(response);
    }
}
