package com.no_plan.library_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "pw", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_num", length = 20)
    private String phoneNum;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "is_admin", columnDefinition = "TINYINT(1) default 0")
    private Boolean isAdmin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();

    public void updateInfo(String password, String name, String phoneNum, String email) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (phoneNum != null && !phoneNum.isEmpty()) {
            this.phoneNum = phoneNum;
        }
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
    }

    public void changeRole(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
