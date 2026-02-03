package com.no_plan.library_api.service;

import com.no_plan.library_api.dto.AiRecommendResponse;
import com.no_plan.library_api.dto.BookMetaResponse;
import com.no_plan.library_api.entity.BookMeta;
import com.no_plan.library_api.repository.BookMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {

    private final BookMetaRepository bookMetaRepository;
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public AiRecommendResponse getRecommendations(String userQuery) {

        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(userQuery).withTopK(5)
        );

        if (similarDocuments.isEmpty()) {
            return AiRecommendResponse.builder()
                    .aiComment("관련된 도서를 찾을 수 없습니다.")
                    .recommendedBooks(List.of())
                    .build();
        }

        List<Long> metaIds = similarDocuments.stream()
                .map(doc -> Long.parseLong(String.valueOf(doc.getMetadata().get("meta_id"))))
                .toList();
        List<BookMeta> books = bookMetaRepository.findAllById(metaIds);

        String bookContext = books.stream()
                .map(book -> String.format("[%d] 제목: %s, 저자: %s, 설명: %s",
                        book.getMetaId(), book.getTitle(), book.getAuthor(), book.getDescription()))
                .collect(Collectors.joining("\n"));

        SystemMessage systemMessage = new SystemMessage(
                "당신은 도서관의 AI 사서입니다. 다음 규칙을 엄격히 따르세요.\n" +
                        "1. 사용자의 질문과 제공된 도서 목록을 비교하여, '직접적으로 관련 있는' 책만 추천하세요.\n" +
                        "2. 관련 없는 책(예: 여행 질문에 딥러닝 책 등)은 절대 추천하지 마세요.\n" +
                        "3. 답변 형식은 다음과 같이 작성하세요:\n" +
                        "   [친절한 추천 사유 설명]\n" +
                        "   ||\n" +
                        "   [추천하는 책의 ID 번호들을 쉼표로 구분]\n\n" +
                        "예시:\n" +
                        "일본 여행을 계획 중이시군요! 리얼 도쿄 책을 추천합니다. || 10, 12\n" +
                        "(만약 추천할 책이 하나도 없다면 ID 부분은 비워두세요)"
        );

        UserMessage userMessage = new UserMessage(
                "사용자 질문: " + userQuery + "\n\n" +
                        "검색된 도서 목록:\n" + bookContext
        );

        String rawResponse = chatClient.call(new Prompt(List.of(systemMessage, userMessage)))
                .getResult().getOutput().getContent();

        String aiComment;
        List<Long> validIds = new ArrayList<>();

        if (rawResponse.contains("||")) {
            String[] parts = rawResponse.split("\\|\\|");
            aiComment = parts[0].trim();

            if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                try {
                    String[] idStrs = parts[1].trim().split(",");
                    for (String idStr : idStrs) {
                        validIds.add(Long.parseLong(idStr.trim()));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ID 파싱 실패: " + parts[1]);
                }
            }
        } else {
            aiComment = rawResponse;
        }

        List<BookMetaResponse> filteredBooks = books.stream()
                .filter(book -> validIds.contains(book.getMetaId()))
                .map(BookMetaResponse::from)
                .collect(Collectors.toList());

        if (aiComment.contains("죄송합니다") || filteredBooks.isEmpty()) {
            filteredBooks = List.of();
        }

        return AiRecommendResponse.builder()
                .aiComment(aiComment)
                .recommendedBooks(filteredBooks)
                .build();
    }
}