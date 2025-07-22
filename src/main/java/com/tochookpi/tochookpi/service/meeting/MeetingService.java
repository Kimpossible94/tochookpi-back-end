package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingService {

    void createMeeting(String loggedInUserEmail, MultipartFile image, MeetingDTO meetingDTO);

    List<MeetingDTO> getMeetings(String loggedInUserEmail, String searchTerm, List<MeetingCategory> category, String sort, String type);

    MeetingDTO getMeetingById(String loggedInUserEmail, Long id);

    void joinMeeting(String loggedInUserEmail, Long id);

    void leaveMeeting(String loggedInUserEmail, Long id);

    void deleteMeeting(String loggedInUserEmail, Long id);
}
