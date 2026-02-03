package com.no_plan.library_api.service;

import com.no_plan.library_api.dto.BookItemResponse;
import com.no_plan.library_api.dto.BookItemUpdateRequest;
import com.no_plan.library_api.dto.BookMetaRequest;
import com.no_plan.library_api.dto.BookMetaResponse;
import com.no_plan.library_api.entity.BookItem;
import com.no_plan.library_api.entity.BookMeta;
import com.no_plan.library_api.repository.BookItemRepository;
import com.no_plan.library_api.repository.BookMetaRepository;
import com.no_plan.library_api.statusEnum.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookMetaRepository bookMetaRepository;
    private final BookItemRepository bookItemRepository;
    private final VectorStore vectorStore;

    @Value("${file.upload-dir}")
    private String uploadDir;
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) return;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능함");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!List.of("jpg", "jpeg", "png", "gif").contains(extension)) {
                throw new IllegalArgumentException("지원하지 않는 이미지 형식");
            }
        }
    }

    /* BookMeta */
    @Transactional
    public BookMetaResponse createBookMeta(BookMetaRequest request, MultipartFile image) {
        String storedImageUrl = request.getImageUrl();

        if (image != null && !image.isEmpty()) {
            validateImageFile(image);

            try {
                String safeUploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
                File directory = new File(safeUploadDir);
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if(!created) {
                        throw new IOException("디렉토리 생성 실패: " + safeUploadDir);
                    }
                }

                String originalFilename = image.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String savedFileName = UUID.randomUUID() + extension;
                String filePath = safeUploadDir + savedFileName;

                image.transferTo(new File(filePath));
                storedImageUrl = "/images/" + savedFileName;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 저장 중 오류 발생", e);
            }
        }

        BookMeta bookMeta = BookMeta.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .category(request.getCategory())
                .imageUrl(storedImageUrl)
                .description(request.getDescription())
                .build();

        BookMeta savedMeta = bookMetaRepository.save(bookMeta);
        Document document = getDocument(savedMeta);
        vectorStore.add(List.of(document));

        return BookMetaResponse.from(savedMeta);
    }

    private Document getDocument(BookMeta savedMeta) {
        String contentToEmbed = "제목: " + savedMeta.getTitle() + "\n" +
                "저자: " + savedMeta.getAuthor() + "\n" +
                "카테고리: " + savedMeta.getCategory() + "\n" +
                "설명: " + savedMeta.getDescription();

        Map<String, Object> metadata = Map.of(
                "meta_id", savedMeta.getMetaId().toString(),
                "category", savedMeta.getCategory() != null ? savedMeta.getCategory() : ""
        );

        return new Document(savedMeta.getMetaId().toString(), contentToEmbed, metadata);
    }

    public Page<BookMetaResponse> searchBooks(String keyword, String category, Pageable pageable) {

        String searchKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;
        String searchCategory = (category != null && !category.isBlank()) ? category : null;

        return bookMetaRepository.search(searchKeyword, searchCategory, pageable)
                .map(BookMetaResponse::from);
    }

    public BookMetaResponse getBookMetaDetail(Long metaId) {
        BookMeta meta = bookMetaRepository.findById(metaId)
                .orElseThrow( () -> new IllegalArgumentException("도서 정보를 찾을 수 없음"));

        return BookMetaResponse.from(meta);
    }

    @Transactional
    public BookMetaResponse updateBookMeta(Long metaId, BookMetaRequest request, MultipartFile image) {

        BookMeta meta = bookMetaRepository.findById(metaId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없음"));

        String storedImageUrl = request.getImageUrl();

        // 새 이미지가 업로드 된 경우
        if (image != null && !image.isEmpty()) {
            validateImageFile(image);

            try {
                String safeUploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";

                // 기존 이미지가 존재한다면 삭제
                if (meta.getImageUrl() != null && meta.getImageUrl().startsWith("/images/")) {
                    String oldFileName = meta.getImageUrl().replace("/images/", "");
                    Path oldFilePath = Paths.get(safeUploadDir + oldFileName);

                    // 파일이 실제로 존재하면 삭제
                    try {
                        Files.deleteIfExists(oldFilePath);
                    } catch (IOException e) {
                        // 삭제 실패
                        System.err.println("기존 이미지 삭제 실패: " + e.getMessage());
                    }
                }

                // 새 이미지 저장
                File directory = new File(safeUploadDir);
                if (!directory.exists()) directory.mkdirs();

                String originalFilename = image.getOriginalFilename();
                String extension = (originalFilename != null && originalFilename.contains(".")) ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String savedFileName = UUID.randomUUID() + extension;
                String filePath = safeUploadDir + savedFileName;

                image.transferTo(new File(filePath));

                storedImageUrl = "/images/" + savedFileName;

            } catch (IOException e) {
                throw new RuntimeException("이미지 수정 중 오류 발생", e);
            }
        } else if (storedImageUrl == null) {
            storedImageUrl = meta.getImageUrl();
        }

        meta.update(
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getCategory(),
                storedImageUrl,
                request.getDescription()
        );

        Document updatedDocument = getDocument(meta);
        vectorStore.add(List.of(updatedDocument));

        return BookMetaResponse.from(meta);
    }

    @Transactional
    public void deleteBookMeta(Long metaId) {
        BookMeta meta = bookMetaRepository.findById(metaId)
                .orElseThrow(() -> new IllegalArgumentException("도서 정보를 찾을 수 없음"));

        // 이미지 파일 삭제 로직
        String imageUrl = meta.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/images/")) {
            try {
                String fileName = imageUrl.replace("/images/", "");
                String safeUploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
                Path filePath = Paths.get(safeUploadDir + fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("이미지 파일 삭제 실패: " + e.getMessage());
            }
        }

        // DB 삭제 (Cascade 설정으로 인해 하위 Item, Loan 모두 삭제됨)
        bookMetaRepository.delete(meta);

        // Vector DB 삭제
        try {
            vectorStore.delete(List.of(String.valueOf(metaId)));
        } catch (Exception e) {
            System.err.println("Vector DB 데이터 삭제 실패 (무시하고 진행): " + e.getMessage());
        }
    }

    /* BookItem */
    @Transactional
    public void addBookItem(Long metaId) {
        BookMeta meta = bookMetaRepository.findById(metaId)
                .orElseThrow( () -> new IllegalArgumentException("도서 정보를 찾을 수 없음"));
        BookItem bookItem = BookItem.builder()
                .bookItemId(UUID.randomUUID().toString())
                .bookMeta(meta)
                .status(ItemStatus.AVAILABLE)
                .location("중앙 서고")
                .build();

        bookItemRepository.save(bookItem);
    }

    public List<BookItemResponse> getBookItems(Long metaId) {
        return bookItemRepository.findByBookMeta_MetaId(metaId).stream()
                .map(BookItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateBookItem(String bookId, BookItemUpdateRequest request) {
        BookItem item = bookItemRepository.findById(bookId)
                .orElseThrow( () -> new IllegalArgumentException("도서를 찾을 수 없음"));

        if (request.getStatus() != null) {
            item.changeStatus(request.getStatus());
        }

        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            item.changeLocation(request.getLocation());
        }
    }

    @Transactional
    public void deleteBookItem(String bookItemId) {
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // Cascade로 인해 연결된 Loan 기록이 함께 삭제됨
        bookItemRepository.delete(bookItem);
    }

}
