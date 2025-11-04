package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingReviewRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingReviewService {
    void createMeetingReview(String loggedInUserId, List<MultipartFile> files, MeetingReviewRequestDTO meetingReviewRequestDTO);

    void deleteMeetingReview(String loggedInUserId, Long id);

    void modifyMeetingReview(String loggedInUserId, Long id, List<MultipartFile> files, MeetingReviewRequestDTO meetingReviewRequestDTO);

    List<MeetingReviewResponseDTO> getReviewsByMeeting(Long meetingId);
}
