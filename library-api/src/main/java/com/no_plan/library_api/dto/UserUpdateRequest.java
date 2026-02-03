package com.no_plan.library_api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String password;
    private String name;
    private String phoneNum;
    private String email;
    private Boolean isAdmin;
}
