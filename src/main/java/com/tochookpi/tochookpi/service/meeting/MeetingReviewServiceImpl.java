package com.tochookpi.tochookpi.service.meeting;

import com.tochookpi.tochookpi.dto.meeting.MeetingReviewRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingReviewResponseDTO;
import com.tochookpi.tochookpi.dto.meeting.ReviewFileDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import com.tochookpi.tochookpi.entity.ReviewFileEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.FileState;
import com.tochookpi.tochookpi.enums.FileType;
import com.tochookpi.tochookpi.exception.S3OrphanFileException;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingReviewRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import com.tochookpi.tochookpi.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MeetingReviewServiceImpl implements MeetingReviewService {
    private final MeetingReviewRepository meetingReviewRepository;
    private final UserService userService;
    private final MeetingService meetingService;
    private final S3Service s3Service;

    @Override
    @Transactional
    public void createMeetingReview(String loggedInUserId, List<MultipartFile> files, MeetingReviewRequestDTO meetingReviewRequestDTO) {
        if(!meetingReviewRequestDTO.getWriterId().equals(loggedInUserId)) new TochookpiException(ErrorCode.USER_NOT_FOUND);

        UserEntity userEntity = userService.getUserEntity(loggedInUserId);
        MeetingEntity meetingEntity = meetingService.getMeetingEntity(meetingReviewRequestDTO.getMeetingId());

        List<String> uploadedUrls = new ArrayList<>();
        List<ReviewFileDTO> reviewFiles = uploadFiles(files, uploadedUrls);
        meetingReviewRequestDTO.setReviewFiles(reviewFiles);

        try {
            MeetingReviewEntity reviewEntity = meetingReviewRequestDTO.toEntity(meetingEntity, userEntity);
            meetingReviewRepository.save(reviewEntity);
        } catch (Exception e) {
            throw new S3OrphanFileException(ErrorCode.REVIEW_FAIL_SAVE_REVIEW, uploadedUrls);
        }
    }

    @Override
    @Transactional
    public void deleteMeetingReview(String loggedInUserId, Long id) {
        UserEntity userEntity = userService.getUserEntity(loggedInUserId);
        MeetingReviewEntity meetingReviewEntity = meetingReviewRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.REVIEW_NOT_FOUND));

        if(!meetingReviewEntity.getWriter().getId().equals(userEntity.getId()))
            throw new TochookpiException(ErrorCode.REVIEW_ONLY_WRITER);

        meetingReviewRepository.delete(meetingReviewEntity);
    }


    @Override
    @Transactional
    public void modifyMeetingReview(String loggedInUserId, Long reviewId, List<MultipartFile> files, MeetingReviewRequestDTO meetingReviewRequestDTO) {
        UserEntity userEntity = userService.getUserEntity(loggedInUserId);
        MeetingEntity meetingEntity = meetingService.getMeetingEntity(meetingReviewRequestDTO.getMeetingId());
        MeetingReviewEntity meetingReviewEntity = meetingReviewRepository.findById(meetingReviewRequestDTO.getId())
                .orElseThrow(() -> new TochookpiException(ErrorCode.REVIEW_NOT_FOUND));

        List<String> uploadedUrls = new ArrayList<>();
        List<ReviewFileDTO> reviewFiles = uploadFiles(files, uploadedUrls);

        try {
            List<ReviewFileEntity> existingFiles = meetingReviewEntity.getFiles();
            // 기존 파일에서 삭제되는 파일처리
            existingFiles.removeIf(f -> meetingReviewRequestDTO.getReviewFiles().stream()
                    .anyMatch(dto -> dto.getId() != null
                            && dto.getState() == FileState.DELETE
                            && dto.getId().equals(f.getId())));

            // 새로 추가된 파일처리
            for (ReviewFileDTO dto : reviewFiles) {
                ReviewFileEntity entity = dto.toEntity();
                meetingReviewEntity.addFile(entity);
            }

            meetingReviewEntity.setComments(meetingReviewRequestDTO.getComments());
            meetingReviewRepository.save(meetingReviewEntity);
        } catch (Exception e) {
            throw new S3OrphanFileException(ErrorCode.REVIEW_FAIL_SAVE_REVIEW, uploadedUrls);
        }
    }

    private List<ReviewFileDTO> uploadFiles(List<MultipartFile> files, List<String> urlList) {
        if (files == null) return new ArrayList<>();
        List<ReviewFileDTO> reviewFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = s3Service.uploadFile(file, "review");
                urlList.add(url);
                ReviewFileDTO dto = new ReviewFileDTO();
                dto.setUrl(url);
                dto.setType(file.getContentType().startsWith("image") ? FileType.IMAGE : FileType.VIDEO);
                reviewFiles.add(dto);
            } catch (Exception e) {
                throw new S3OrphanFileException(ErrorCode.REVIEW_FAIL_SAVE_REVIEW, urlList);
            }
        }
        return reviewFiles;
    }

    private void convertToEntitiesAndAdd(MeetingReviewEntity review, List<ReviewFileDTO> dtos) {
        for (ReviewFileDTO dto : dtos) {
            ReviewFileEntity entity = dto.toEntity();
            review.addFile(entity); // 연관관계 편의 메서드 사용
        }
    }

    public List<MeetingReviewResponseDTO> getReviewsByMeeting(Long meetingId) {
        MeetingEntity meetingEntity = meetingService.getMeetingEntity(meetingId);

        return meetingReviewRepository.findByMeeting(meetingEntity)
                .stream()
                .map(MeetingReviewEntity::toDTO)
                .toList();
    }
}