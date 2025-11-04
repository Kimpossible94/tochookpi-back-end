package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingReviewResponseDTO {
    private Long id;
    private Long meetingId;
    private UserDTO writer;
    private List<ReviewFileDTO> files;
    private String comments;
    private LocalDateTime createdAt;

    public MeetingReviewEntity toEntity(MeetingEntity meetingEntity, UserEntity userEntity) {
        MeetingReviewEntity entity = MeetingReviewEntity.builder()
                .meeting(meetingEntity)
                .writer(userEntity)
                .comments(this.comments)
                .createdAt(LocalDateTime.now())
                .build();

        entity.setFiles(this.files.stream()
                .map(ReviewFileDTO::toEntity)
                .collect(Collectors.toList()));
        return entity;
    }
}
