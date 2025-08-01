package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.meeting.MeetingRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingResponseDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
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
                                              @RequestPart("meeting") MeetingRequestDTO meetingDTO) {

        String loggedInUserId = customUserDetails.getUsername();
        meetingService.createMeeting(loggedInUserId, image, meetingDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponseDTO>> getMeetings(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestParam(required = false) String searchTerm,
                                                                    @RequestParam(required = false) List<MeetingCategory> category,
                                                                    @RequestParam(required = false) String sort,
                                                                    @RequestParam(required = false) String type)
    {
        String loggedInUserId = customUserDetails.getUsername();
        return ResponseEntity.ok(meetingService.getMeetings(loggedInUserId, searchTerm, category, sort, type));
    }

    @GetMapping("{id}")
    public ResponseEntity<MeetingResponseDTO> getMeetingById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable("id") Long id) {
        String loggedInUserId = customUserDetails.getUsername();
        return ResponseEntity.ok(meetingService.getMeetingById(loggedInUserId, id));
    }

    @PutMapping("{id}")
    public ResponseEntity<MeetingResponseDTO> modifyMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable("id") Long id,
                                                            @RequestPart(value = "image", required = false) MultipartFile image,
                                                            @RequestPart("meeting") MeetingRequestDTO newMeeting) {
        String loggedInUserId = customUserDetails.getUsername();
        meetingService.modifyMeeting(loggedInUserId, id, image, newMeeting);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/join")
    public ResponseEntity<Void> joinMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable("id") Long id) {
        String loggedInUserId = customUserDetails.getUsername();
        meetingService.joinMeeting(loggedInUserId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/leave")
    public ResponseEntity<Void> leaveMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable("id") Long id) {
        String loggedInUserId = customUserDetails.getUsername();
        meetingService.leaveMeeting(loggedInUserId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable("id") Long id) {
        String loggedInUserId = customUserDetails.getUsername();
        meetingService.deleteMeeting(loggedInUserId, id);

        return ResponseEntity.ok().build();
    }
}
