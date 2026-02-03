package com.no_plan.library_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Long metaId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(length = 50)
    private String category;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "bookMeta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookItem> bookItems = new ArrayList<>();

    public void update(String title, String author, String publisher,
                       String category, String imageUrl, String description) {
        if (title != null) this.title = title;
        if (author != null) this.author = author;
        if (publisher != null) this.publisher = publisher;
        if (category != null) this.category = category;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (description != null) this.description = description;
    }
}
