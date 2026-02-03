package com.no_plan.library_api.repository;

import com.no_plan.library_api.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("SELECT l FROM Loan l JOIN FETCH l.bookItem bi JOIN FETCH bi.bookMeta m WHERE l.user.userId = :userId")
    List<Loan> findByUser_UserId(@Param("userId") Long userId);
}