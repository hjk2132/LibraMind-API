package com.no_plan.library_api.dto;

import com.no_plan.library_api.statusEnum.ItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookItemUpdateRequest {
    private ItemStatus status;
    private String location;
}
