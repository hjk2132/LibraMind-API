package com.no_plan.library_api.dto;

import com.no_plan.library_api.entity.BookMeta;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookMetaResponse {
    private Long metaId;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String imageUrl;
    private String description;

    public static BookMetaResponse from(BookMeta meta) {
        return BookMetaResponse.builder()
                .metaId(meta.getMetaId())
                .title(meta.getTitle())
                .author(meta.getAuthor())
                .publisher(meta.getPublisher())
                .category(meta.getCategory())
                .imageUrl(meta.getImageUrl())
                .description(meta.getDescription())
                .build();
    }
}
