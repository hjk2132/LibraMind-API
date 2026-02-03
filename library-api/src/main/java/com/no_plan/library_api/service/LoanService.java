package com.no_plan.library_api.service;

import com.no_plan.library_api.dto.LoanDetailResponse;
import com.no_plan.library_api.entity.BookItem;
import com.no_plan.library_api.entity.Loan;
import com.no_plan.library_api.entity.User;
import com.no_plan.library_api.repository.BookItemRepository;
import com.no_plan.library_api.repository.LoanRepository;
import com.no_plan.library_api.repository.UserRepository;
import com.no_plan.library_api.statusEnum.ItemStatus;
import com.no_plan.library_api.statusEnum.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookItemRepository bookItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoanDetailResponse loanBook(Long userId, String bookItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new IllegalArgumentException("사용자를 찾을 수 없음"));
        BookItem bookItem = bookItemRepository.findByIdWithLock(bookItemId)
                .orElseThrow( () -> new IllegalArgumentException("도서를 찾을 수 없음"));

        if (bookItem.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalStateException("현재 대출 불가능한 도서");
        }

        Loan loan = Loan.builder()
                .user(user)
                .bookItem(bookItem)
                .loanDate(LocalDate.now())
                // 대여기간 2주로 설정
                .dueDate(LocalDate.now().plusDays(14))
                .status(LoanStatus.LOANED)
                .build();

        loanRepository.save(loan);
        bookItem.changeStatus(ItemStatus.BORROWED);

        return LoanDetailResponse.from(loan);
    }

    @Transactional
    public void returnBook(Long loanId, Long currentUserId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없음"));

        if (!loan.getUser().getUserId().equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

            if (!currentUser.getIsAdmin()) {
                throw new IllegalStateException("본인의 대출 기록만 반납할 수 있음");
            }
        }

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("이미 반납한 도서임");
        }

        loan.returnBook();

        BookItem bookItem = loan.getBookItem();
        bookItem.changeStatus(ItemStatus.AVAILABLE);
    }

    public List<LoanDetailResponse> getUserLoanHistory(Long userId) {
        return loanRepository.findByUser_UserId(userId).stream()
                .map(LoanDetailResponse::from)
                .collect(Collectors.toList());
    }

    public LoanDetailResponse getLoanDetail(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow( () -> new IllegalArgumentException("대출 기록을 찾을 수 없음"));

        return LoanDetailResponse.from(loan);
    }
}
