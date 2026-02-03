package com.no_plan.library_api.security;

import com.no_plan.library_api.entity.User;
import com.no_plan.library_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdString) throws UsernameNotFoundException {
        Long userId = Long.parseLong(userIdString);

        User user = userRepository.findById(userId)
                .orElseThrow( () -> new UsernameNotFoundException("사융자를 찾을 수 없음"));

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserId()),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getIsAdmin() ? "ROLE_ADMIN" : "ROLE_USER")));
    }
}
