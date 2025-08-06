package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingReviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingReviewService {
    void createMeetingReview(String loggedInUserId, List<MultipartFile> files, MeetingReviewDTO meetingReviewDTO);
}
