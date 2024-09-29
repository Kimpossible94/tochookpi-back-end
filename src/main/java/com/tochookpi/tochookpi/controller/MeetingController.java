package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.service.MeetingServiceImpl;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "meetings")
public class MeetingController {
    private final MeetingServiceImpl meetingService;

    public MeetingController(MeetingServiceImpl meetingService) {
        this.meetingService = meetingService;
    }
}
