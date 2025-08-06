package com.tochookpi.tochookpi.service.meeting;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.S3OrphanFileException;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingRepository;
import com.tochookpi.tochookpi.repository.MeetingReviewRepository;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MeetingReviewServiceImpl implements MeetingReviewService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingReviewRepository meetingReviewRepository;
    private final S3Service s3Service;
    private final JPAQueryFactory queryFactory;

    @Override
    public void createMeetingReview(String loggedInUserId, List<MultipartFile> files, MeetingReviewDTO meetingReviewDTO) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(meetingReviewDTO.getMeetingId())
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        meetingReviewDTO.setWriterId(Long.valueOf(loggedInUserId));

        List<String> urlList = new ArrayList<>();
        if(files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String imageUrl = s3Service.uploadFile(file, "review");
                    urlList.add(imageUrl);
                } catch (Exception e) {
                    throw new S3OrphanFileException(ErrorCode.MEETING_FAIL_SAVE_MEETING, urlList);
                }
            }
        }
        meetingReviewDTO.setFiles(urlList);

        try {
            MeetingReviewEntity meetingReviewEntity = meetingReviewDTO.toEntity(meetingEntity, userEntity);
            meetingReviewRepository.save(meetingReviewEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TochookpiException(ErrorCode.MEETING_FAIL_SAVE_MEETING);
        }
    }
}
