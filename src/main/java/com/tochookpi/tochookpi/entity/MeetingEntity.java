package com.tochookpi.tochookpi.entity;

import com.tochookpi.tochookpi.dto.meeting.LocationDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingResponseDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserEntity organizer;

    private String image;

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
    private List<MeetingParticipantEntity> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingReviewEntity> reviews = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public MeetingResponseDTO toDTO() {
        MeetingResponseDTO dto = new MeetingResponseDTO();
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setDescription(this.description);
        dto.setCategory(this.category);
        dto.setImage(this.image);
        dto.setOrganizer(new UserDTO(this.organizer));
        dto.setStartDate(this.startDate);
        dto.setEndDate(this.endDate);
        dto.setStatus(this.status);

        // Location 변환
        if (this.location != null) {
            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setTitle(this.location.getTitle());
            locationDTO.setAddress(this.location.getAddress());
            locationDTO.setLng(this.location.getLng());
            locationDTO.setLat(this.location.getLat());
            dto.setLocation(locationDTO);
        }

        // participants 변환
        dto.setParticipants(this.participants.stream()
                .map(participant -> new UserDTO(participant.getUser()))
                .collect(Collectors.toList()));

        return dto;
    }
}