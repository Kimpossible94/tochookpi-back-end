package com.tochookpi.tochookpi.entity;

import com.tochookpi.tochookpi.dto.meeting.MeetingReviewResponseDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "meeting_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MeetingReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private MeetingEntity meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity writer;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewFileEntity> files = new ArrayList<>();

    @Column(nullable = false, length = 1000)
    private String comments;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public void addFile(ReviewFileEntity file) {
        this.files.add(file);
        file.setReview(this);
    }
    public MeetingReviewResponseDTO toDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(this.writer.getId());
        userDTO.setProfileImage(this.writer.getProfileImage());
        userDTO.setUsername(this.writer.getName());

        MeetingReviewResponseDTO dto = MeetingReviewResponseDTO.builder()
                .id(this.id)
                .meetingId(this.meeting.getId())
                .writer(userDTO)
                .comments(this.comments)
                .createdAt(this.createdAt)
                .build();

        // files 변환
        dto.setFiles(this.files.stream()
                .map(file -> file.toDTO())
                .collect(Collectors.toList()));

        return dto;
    }
}