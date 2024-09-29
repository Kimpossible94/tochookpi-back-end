package com.tochookpi.tochookpi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meetings")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String meetingName;
    private String location;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer; // 모임 주최자
}
