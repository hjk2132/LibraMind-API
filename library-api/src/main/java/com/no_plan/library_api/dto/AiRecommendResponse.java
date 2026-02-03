package com.no_plan.library_api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiRecommendResponse {
    private String aiComment;
    private List<BookMetaResponse> recommendedBooks;
}
