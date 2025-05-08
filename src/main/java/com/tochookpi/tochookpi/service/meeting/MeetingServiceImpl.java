package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingScheduleEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingRepository;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final S3Service s3Service;

    public MeetingServiceImpl(UserRepository userRepository, MeetingRepository meetingRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void createMeeting(String loggedInUserEmail, MultipartFile image, MeetingDTO meetingDTO) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingDTO.toEntity(userEntity);
        List<MeetingScheduleEntity> scheduleEntities = meetingDTO.toScheduleEntities(meetingEntity);
        meetingEntity.setSchedules(scheduleEntities);

        String imageUrl = s3Service.uploadFile(image, "meeting");

//        meetingEntity.setImage(imageUrl);
//        meetingRepository.save(meetingEntity);
    }
}
