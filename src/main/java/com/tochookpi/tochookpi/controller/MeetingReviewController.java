package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewRequestDTO;
import com.tochookpi.tochookpi.service.meeting.MeetingReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("reviews")
public class MeetingReviewController {
    private final MeetingReviewService meetingReviewService;

    @PostMapping
    public ResponseEntity<Void> createMeetingReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                              @RequestPart("review") MeetingReviewRequestDTO meetingReviewRequestDTO) {

        String loggedInUserId = customUserDetails.getUsername();
        meetingReviewService.createMeetingReview(loggedInUserId, files, meetingReviewRequestDTO);
        return ResponseEntity.ok().build();
    }
}
