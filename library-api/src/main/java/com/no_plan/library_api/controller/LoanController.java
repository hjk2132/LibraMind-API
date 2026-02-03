package com.no_plan.library_api.controller;

import com.no_plan.library_api.dto.LoanDetailResponse;
import com.no_plan.library_api.dto.LoanRequest;
import com.no_plan.library_api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDetailResponse> createLoan(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LoanRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        LoanDetailResponse response = loanService.loanBook(userId, request.getBookItemId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<Void> returnBook(
            @PathVariable Long loanId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        loanService.returnBook(loanId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<LoanDetailResponse>> getMyLoans(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<LoanDetailResponse> history = loanService.getUserLoanHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDetailResponse> getLoanDetail(@PathVariable Long loanId) {
        LoanDetailResponse response = loanService.getLoanDetail(loanId);
        return ResponseEntity.ok(response);
    }

}