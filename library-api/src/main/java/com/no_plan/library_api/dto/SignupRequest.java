package com.no_plan.library_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String id;
    private String password;
    private String name;
    private String phoneNum;
    private String email;
}
