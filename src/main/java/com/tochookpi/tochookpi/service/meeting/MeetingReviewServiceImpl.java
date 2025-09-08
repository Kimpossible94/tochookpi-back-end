package com.tochookpi.tochookpi.service.meeting;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.ReviewFileDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.FileType;
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
    public void createMeetingReview(String loggedInUserId, List<MultipartFile> files, MeetingReviewRequestDTO meetingReviewRequestDTO) {
        if(!meetingReviewRequestDTO.getWriterId().equals(loggedInUserId)) new TochookpiException(ErrorCode.USER_NOT_FOUND);
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(meetingReviewRequestDTO.getMeetingId())
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        List<String> urlList = new ArrayList<>();
        List<ReviewFileDTO> reviewFiles = new ArrayList<>();
        if(files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    String imageUrl = s3Service.uploadFile(file, "review");
                    urlList.add(imageUrl);

                    boolean isImage = file.getContentType().startsWith("image");
                    ReviewFileDTO reviewFileDTO = new ReviewFileDTO();
                    reviewFileDTO.setUrl(imageUrl);
                    reviewFileDTO.setType(isImage ? FileType.IMAGE : FileType.VIDEO);
                    reviewFiles.add(reviewFileDTO);
                } catch (Exception e) {
                    throw new S3OrphanFileException(ErrorCode.MEETING_FAIL_SAVE_MEETING, urlList);
                }
            }
        }
        meetingReviewRequestDTO.setFiles(reviewFiles);

        try {
            MeetingReviewEntity meetingReviewEntity = meetingReviewRequestDTO.toEntity(meetingEntity, userEntity);
            meetingReviewRepository.save(meetingReviewEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TochookpiException(ErrorCode.MEETING_FAIL_SAVE_MEETING);
        }
    }
}


// TODO: 모임후기 목록 불러오기 미팅 불러올 때 확인
// TODO: 모임후기 entity 타입 추가(이미지, 영상) (완료)
// TODO: 모임 후기 수정
// TODO: 모임 후기 삭제