package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingReviewRequestDTO {
    private Long id;
    private Long meetingId;
    private Long writerId;
    private List<ReviewFileDTO> reviewFiles;
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

        if (this.reviewFiles != null && !this.reviewFiles.isEmpty()) {
            this.reviewFiles.forEach(fileDTO -> meetingReviewEntity.addFile(fileDTO.toEntity()));
        }

        return meetingReviewEntity;
    }
}
