package com.no_plan.library_api.controller;

import com.no_plan.library_api.dto.BookItemResponse;
import com.no_plan.library_api.dto.BookItemUpdateRequest;
import com.no_plan.library_api.dto.BookMetaRequest;
import com.no_plan.library_api.dto.BookMetaResponse;
import com.no_plan.library_api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /*BookMeta*/

    @PostMapping(value = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookMetaResponse> createBookMeta(
            @RequestPart("request") BookMetaRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        BookMetaResponse response = bookService.createBookMeta(request, image);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books")
    public ResponseEntity<Page<BookMetaResponse>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        Page<BookMetaResponse> books = bookService.searchBooks(keyword, category, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/{metaId}")
    public ResponseEntity<BookMetaResponse> getBookMetaDetail(@PathVariable Long metaId) {
        BookMetaResponse response = bookService.getBookMetaDetail(metaId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/books/{metaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookMetaResponse> updateBookMeta(
            @PathVariable Long metaId,
            @RequestPart("request") BookMetaRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        BookMetaResponse response = bookService.updateBookMeta(metaId, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/books/{metaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBookMeta(@PathVariable Long metaId) {
        bookService.deleteBookMeta(metaId);
        return ResponseEntity.ok().build();
    }

    /*BookItem*/

    @PostMapping("/books/{metaId}/items")
    public ResponseEntity<Void> addBookItem(@PathVariable Long metaId) {
        bookService.addBookItem(metaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/{metaId}/items")
    public ResponseEntity<List<BookItemResponse>> getBookItems(@PathVariable Long metaId) {
        List<BookItemResponse> items = bookService.getBookItems(metaId);
        return ResponseEntity.ok(items);
    }

    @PatchMapping("/book-items/{bookId}")
    public ResponseEntity<Void> updateBookItemStatus(
            @PathVariable String bookId,
            @RequestBody BookItemUpdateRequest request) {
        bookService.updateBookItem(bookId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/book-items/{bookItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBookItem(@PathVariable String bookItemId) {
        bookService.deleteBookItem(bookItemId);
        return ResponseEntity.ok().build();
    }

}
