package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeetingService {

    void createMeeting(String loggedInUserEmail, MultipartFile image, MeetingDTO meetingDTO);

    List<MeetingDTO> getMeetings(String searchTerm, List<String> category, String sort);
}
