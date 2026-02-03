package com.no_plan.library_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMetaRequest {
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String imageUrl;
    private String description;
}
