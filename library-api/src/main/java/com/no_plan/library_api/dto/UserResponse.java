package com.no_plan.library_api.dto;

import com.no_plan.library_api.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private String phoneNum;
    private Boolean isAdmin;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .isAdmin(user.getIsAdmin())
                .build();
    }
}
