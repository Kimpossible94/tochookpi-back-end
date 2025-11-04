package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import com.tochookpi.tochookpi.enums.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponseDTO {
    private Long id;
    private String title;
    private String description;
    private MeetingCategory category;
    private UserDTO organizer;
    private String image;
    private LocationDTO location;
    private LocalDate startDate;
    private LocalDate endDate;
    private MeetingStatus status;
    private List<UserDTO> participants;
    private List<MeetingReviewResponseDTO> reviews;
    private boolean isParticipating;
}
