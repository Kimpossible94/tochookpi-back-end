package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.CustomUserDetails;
import com.tochookpi.tochookpi.dto.MeetingDTO;
import com.tochookpi.tochookpi.service.MeetingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("meetings")
public class MeetingController {
    private final MeetingServiceImpl meetingService;

    public MeetingController(MeetingServiceImpl meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("join")
    public ResponseEntity<Void> testParticipant(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @RequestBody MeetingDTO meetingDTO) {
        return ResponseEntity.ok().build();
    }
}
