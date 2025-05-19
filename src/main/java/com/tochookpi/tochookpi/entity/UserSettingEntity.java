package com.tochookpi.tochookpi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isNotificationDisabled = false;

    @Column(nullable = false)
    private boolean isInviteDisabled = false;
}
