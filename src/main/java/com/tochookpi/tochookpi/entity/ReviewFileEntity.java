package com.tochookpi.tochookpi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tochookpi.tochookpi.dto.meeting.ReviewFileDTO;
import com.tochookpi.tochookpi.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_review_files")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@SQLDelete(sql = "UPDATE meeting_review_files SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ReviewFileDTO toDTO() {
        return ReviewFileDTO.builder()
                .url(this.url)
                .type(this.type)
                .build();
    }
}
