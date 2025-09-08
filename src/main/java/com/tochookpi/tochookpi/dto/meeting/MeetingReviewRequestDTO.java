package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.ReviewFileEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingReviewRequestDTO {
    private Long id;
    private Long meetingId;
    private Long writerId;
    private List<ReviewFileDTO> files;
    private String comments;
    private LocalDateTime createdAt;

    public MeetingReviewEntity toEntity(MeetingEntity meetingEntity, UserEntity userEntity) {
        MeetingReviewEntity meetingReviewEntity = MeetingReviewEntity.builder()
                .meeting(meetingEntity)
                .writer(userEntity)
                .files(new ArrayList<>())
                .comments(this.comments)
                .createdAt(LocalDateTime.now())
                .build();

        if (this.files != null && !this.files.isEmpty()) {
            this.files.forEach(fileDTO -> meetingReviewEntity.addFile(fileDTO.toEntity()));
        }

        return meetingReviewEntity;
    }
}
