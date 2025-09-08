package com.tochookpi.tochookpi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tochookpi.tochookpi.dto.meeting.ReviewFileDTO;
import com.tochookpi.tochookpi.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_review_files")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReviewFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType type;

    @Column(name = "file_url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private MeetingReviewEntity review;

    public ReviewFileDTO toDTO() {
        return ReviewFileDTO.builder()
                .url(this.url)
                .type(this.type)
                .build();
    }
}
