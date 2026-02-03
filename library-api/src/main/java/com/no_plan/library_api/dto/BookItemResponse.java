package com.no_plan.library_api.dto;

import com.no_plan.library_api.entity.BookItem;
import com.no_plan.library_api.statusEnum.ItemStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookItemResponse {
    private String bookItemId;
    private Long bookMetaId;
    private ItemStatus status;
    private String location;

    public static BookItemResponse from(BookItem bookItem) {
        return BookItemResponse.builder()
                .bookItemId(bookItem.getBookItemId())
                .bookMetaId(bookItem.getBookMeta().getMetaId())
                .status(bookItem.getStatus())
                .location(bookItem.getLocation())
                .build();
    }
}
