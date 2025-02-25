package com.tochookpi.tochookpi.entity;

import com.tochookpi.tochookpi.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(name = "profile_image", nullable = true, columnDefinition = "VARCHAR(512) DEFAULT ''")
    private String profileImage;

    @Column(columnDefinition = "VARCHAR(2048) DEFAULT ''")
    private String bio;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public UserEntity(String email, String password, String name, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }
}
