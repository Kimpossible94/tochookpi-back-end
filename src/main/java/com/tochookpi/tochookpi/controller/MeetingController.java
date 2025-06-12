package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.service.meeting.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("meetings")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<Void> createMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestPart(value = "image", required = false) MultipartFile image,
                                              @RequestPart("meeting") MeetingDTO meetingDTO) {

        String loggedInUserEmail = customUserDetails.getUsername();
        meetingService.createMeeting(loggedInUserEmail, image, meetingDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getMeetings(@RequestParam(required = false) String searchTerm,
                                                        @RequestParam(required = false) List<String> category,
                                                        @RequestParam(required = false) String sort)
    {
        return ResponseEntity.ok(meetingService.getMeetings(searchTerm, category, sort));
    }

    @GetMapping("{id}")
    public ResponseEntity<MeetingDTO> getMeetingById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }
}
