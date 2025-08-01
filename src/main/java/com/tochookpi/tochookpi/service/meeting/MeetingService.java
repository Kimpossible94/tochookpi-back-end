package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingResponseDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingService {

    void createMeeting(String loggedInUserId, MultipartFile image, MeetingRequestDTO meetingDTO);

    List<MeetingResponseDTO> getMeetings(String loggedInUserId, String searchTerm, List<MeetingCategory> category, String sort, String type);

    MeetingResponseDTO getMeetingById(String loggedInUserId, Long id);

    void modifyMeeting(String loggedInUserId, Long id, MultipartFile image, MeetingRequestDTO newMeeting);

    void joinMeeting(String loggedInUserId, Long id);

    void leaveMeeting(String loggedInUserId, Long id);

    void deleteMeeting(String loggedInUserId, Long id);
}
