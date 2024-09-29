package com.tochookpi.tochookpi.dto;

import com.tochookpi.tochookpi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MeetingDTO {
    private Long id;
    private String meetingName;
    private String location;
    private User organizer; // 모임 주최자
}
