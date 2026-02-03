package com.no_plan.library_api.controller;

import com.no_plan.library_api.dto.UserResponse;
import com.no_plan.library_api.dto.UserRoleUpdateRequest;
import com.no_plan.library_api.dto.UserUpdateRequest;
import com.no_plan.library_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* 일반 사용자용 */

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        UserResponse response = userService.getUserInfo(currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody UserUpdateRequest request) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        UserResponse response = userService.updateUserInfo(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        userService.withdrawUser(currentUserId);
        return ResponseEntity.ok().build();
    }

    /* 관리자용 */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserDetail(@PathVariable Long userId) {
        UserResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest request) {
        userService.updateUserRole(userId, request.getIsAdmin());
        return ResponseEntity.ok().build();
    }

}