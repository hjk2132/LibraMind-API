package com.no_plan.library_api.dto;

import com.no_plan.library_api.entity.Loan;
import com.no_plan.library_api.statusEnum.LoanStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class LoanDetailResponse {
    private Long loanId;
    private String bookItemId;
    private String bookTitle;
    private String bookAuthor;
    private String userId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;

    public static LoanDetailResponse from(Loan loan) {
        return LoanDetailResponse.builder()
                .loanId(loan.getLoanId())
                .bookItemId(loan.getBookItem().getBookItemId())
                .bookTitle(loan.getBookItem().getBookMeta().getTitle())
                .bookAuthor(loan.getBookItem().getBookMeta().getAuthor())
                .userId(loan.getUser().getLoginId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
    }
}
