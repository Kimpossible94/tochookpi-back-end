package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingReviewDTO {
    private Long id;
    private Long meetingId;
    private Long writerId;
    private List<String> files;
    private String comments;
    private LocalDateTime createdAt;

    public MeetingReviewEntity toEntity(MeetingEntity meetingEntity, UserEntity userEntity) {
        return MeetingReviewEntity.builder()
                .meeting(meetingEntity)
                .writer(userEntity)
                .images(this.files)
                .comments(this.comments)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
