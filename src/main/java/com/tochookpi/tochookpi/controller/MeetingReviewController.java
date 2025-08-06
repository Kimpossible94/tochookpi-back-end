package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.meeting.MeetingRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingResponseDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import com.tochookpi.tochookpi.service.meeting.MeetingReviewService;
import com.tochookpi.tochookpi.service.meeting.MeetingService;
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
                                              @RequestPart("review") MeetingReviewDTO meetingReviewDTO) {

        String loggedInUserId = customUserDetails.getUsername();
        meetingReviewService.createMeetingReview(loggedInUserId, files, meetingReviewDTO);
        return ResponseEntity.ok().build();
    }
}
