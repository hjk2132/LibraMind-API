package com.no_plan.library_api.repository;

import com.no_plan.library_api.entity.BookMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookMetaRepository extends JpaRepository<BookMeta, Long> {
    @Query("SELECT book FROM BookMeta book WHERE " +
            "(:category IS NULL OR book.category = :category) AND " +
            "(:keyword IS NULL OR " +
            " book.title LIKE CONCAT('%', :keyword, '%') OR " +
            " book.author LIKE CONCAT('%', :keyword, '%') OR " +
            " book.publisher LIKE CONCAT('%', :keyword, '%'))")
    Page<BookMeta> search(@Param("keyword") String keyword,
                          @Param("category") String category,
                          Pageable pageable);
}