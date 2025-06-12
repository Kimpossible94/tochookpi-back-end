package com.tochookpi.tochookpi.entity;

import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "meetings")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MeetingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserEntity organizer;

    private String image;

    @Column(nullable = false)
    private int maxParticipantsCnt;

    @Column(nullable = false)
    private int currentParticipantsCnt;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingScheduleEntity> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingParticipantEntity> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingReviewEntity> reviews = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public MeetingDTO toDTO() {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setDescription(this.description);
        dto.setImage(this.image);
        dto.setOrganizer(new UserDTO(this.organizer));
        dto.setMaxParticipantsCnt(this.maxParticipantsCnt);
        dto.setCurrentParticipantsCnt(this.currentParticipantsCnt);
        dto.setStartDate(this.startDate);
        dto.setEndDate(this.endDate);
        dto.setStatus(this.status);

        // Location 변환
        MeetingDTO.LocationDTO locationDTO = new MeetingDTO.LocationDTO();
        locationDTO.setTitle(this.location.getTitle());
        locationDTO.setAddress(this.location.getAddress());
        locationDTO.setLng(this.location.getLng());
        locationDTO.setLat(this.location.getLat());
        dto.setLocation(locationDTO);

        // Schedule 변환
        List<MeetingDTO.ScheduleDTO> scheduleDTOList = this.schedules.stream()
                .collect(Collectors.groupingBy(s -> s.getDate().toString()))
                .entrySet().stream()
                .map(entry -> {
                    MeetingDTO.ScheduleDTO scheduleDTO = new MeetingDTO.ScheduleDTO();
                    scheduleDTO.setDate(entry.getKey());

                    List<MeetingDTO.ScheduleDTO.EventDTO> eventDTOs = entry.getValue().stream()
                            .map(schedule -> {
                                MeetingDTO.ScheduleDTO.EventDTO eventDTO = new MeetingDTO.ScheduleDTO.EventDTO();
                                eventDTO.setStartTime(schedule.getStartTime().toString());
                                eventDTO.setEndTime(schedule.getEndTime().toString());
                                eventDTO.setDescription(schedule.getDescription());
                                return eventDTO;
                            }).toList();

                    scheduleDTO.setEvents(eventDTOs);
                    return scheduleDTO;
                }).toList();

        dto.setSchedules(scheduleDTOList);

        return dto;
    }
}