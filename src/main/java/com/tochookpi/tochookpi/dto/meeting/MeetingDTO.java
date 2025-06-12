package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.entity.Location;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingScheduleEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDTO {
    private Long id;
    private String title;
    private String description;
    private UserDTO organizer;
    private String image;
    private LocationDTO location;
    private int maxParticipantsCnt;
    private int currentParticipantsCnt;
    private LocalDate startDate;
    private LocalDate endDate;
    private MeetingStatus status;
    private List<ScheduleDTO> schedules;

    @Getter
    @Setter
    public static class LocationDTO {
        private String title;
        private String address;
        private Double lng;
        private Double lat;
    }

    @Getter
    @Setter
    public static class ScheduleDTO {
        private String date;
        private List<EventDTO> events;

        @Getter
        @Setter
        public static class EventDTO {
            private String startTime;
            private String endTime;
            private String description;
        }
    }

    public MeetingEntity toEntity(UserEntity organizer) {
        return MeetingEntity.builder()
                .title(this.title)
                .description(this.description)
                .image(this.image)
                .organizer(organizer)
                .maxParticipantsCnt(this.maxParticipantsCnt)
                .currentParticipantsCnt(this.currentParticipantsCnt == 0 ? 1 : this.currentParticipantsCnt)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status != null ? this.status : MeetingStatus.BEFORE)
                .location(Location.builder()
                        .title(this.location.getTitle())
                        .address(this.location.getAddress())
                        .lng(this.location.getLng())
                        .lat(this.location.getLat())
                        .build())
                .build();
    }

    public List<MeetingScheduleEntity> toScheduleEntities(MeetingEntity meeting) {
        return this.schedules.stream()
                .flatMap(scheduleDTO -> scheduleDTO.getEvents().stream()
                        .map(eventDTO -> {
                            return MeetingScheduleEntity.builder()
                                    .meeting(meeting)
                                    .date(LocalDate.parse(scheduleDTO.getDate()))
                                    .startTime(LocalTime.parse(eventDTO.getStartTime()))
                                    .endTime(LocalTime.parse(eventDTO.getEndTime()))
                                    .description(eventDTO.getDescription())
                                    .build();
                        })
                )
                .toList();
    }
}
