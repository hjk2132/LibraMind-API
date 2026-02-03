package com.no_plan.library_api.repository;

import com.no_plan.library_api.entity.BookItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookItemRepository extends JpaRepository<BookItem, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BookItem b where b.bookItemId = :id")
    Optional<BookItem> findByIdWithLock(@Param("id") String id);

    List<BookItem> findByBookMeta_MetaId(Long metaId);
}