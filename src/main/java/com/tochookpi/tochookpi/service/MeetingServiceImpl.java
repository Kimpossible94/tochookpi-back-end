package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.repository.MeetingRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }
}
