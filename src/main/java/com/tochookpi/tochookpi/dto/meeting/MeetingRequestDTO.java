package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import com.tochookpi.tochookpi.enums.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRequestDTO {
    private Long id;
    private String title;
    private String description;
    private MeetingCategory category;
    private String image;
    private LocationDTO location;
    private LocalDate startDate;
    private LocalDate endDate;
    private MeetingStatus status;
    private List<Long> participants;

    public MeetingEntity toEntity(UserEntity organizer) {
        MeetingEntity meetingEntity = MeetingEntity.builder()
                .title(this.title)
                .description(this.description)
                .category(this.category)
                .image(this.image)
                .organizer(organizer)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status != null ? this.status : MeetingStatus.BEFORE)
                .participants(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();

        if(this.location != null) {
            meetingEntity.setLocation(this.location.toLocation());
        }

        return meetingEntity;
    }
}
