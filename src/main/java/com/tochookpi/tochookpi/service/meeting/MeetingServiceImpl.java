package com.tochookpi.tochookpi.service.meeting;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingScheduleEntity;
import com.tochookpi.tochookpi.entity.QMeetingEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.SortOption;
import com.tochookpi.tochookpi.exception.S3OrphanFileException;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingRepository;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final S3Service s3Service;
    private final JPAQueryFactory queryFactory;

    public MeetingServiceImpl(UserRepository userRepository,
                              MeetingRepository meetingRepository,
                              S3Service s3Service,
                              JPAQueryFactory queryFactory) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.s3Service = s3Service;
        this.queryFactory = queryFactory;
    }

    @Override
    public void createMeeting(String loggedInUserEmail, MultipartFile image, MeetingDTO meetingDTO) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingDTO.toEntity(userEntity);
        List<MeetingScheduleEntity> scheduleEntities = meetingDTO.toScheduleEntities(meetingEntity);
        meetingEntity.setSchedules(scheduleEntities);

        String imageUrl = null;
        if(image != null) {
            imageUrl = s3Service.uploadFile(image, "meeting");
            meetingEntity.setImage(imageUrl);
        }

        try {
            meetingRepository.save(meetingEntity);
        } catch (Exception e) {
            if (imageUrl != null) {
                throw new S3OrphanFileException(ErrorCode.MEETING_FAIL_CREATE_MEETING, imageUrl);
            }
            throw new TochookpiException(ErrorCode.MEETING_FAIL_CREATE_MEETING);
        }
    }

    @Override
    public List<MeetingDTO> getMeetings(String searchTerm, List<String> category, String sort) {
        QMeetingEntity meetingEntity = QMeetingEntity.meetingEntity;
        BooleanBuilder predicate = new BooleanBuilder();

        if(searchTerm != null && !searchTerm.isEmpty()) {
            predicate.and(meetingEntity.title.containsIgnoreCase(searchTerm));
        }

        // 카테고리 추후 추가 예정
//        if(category != null && !category.isEmpty()) {
//            predicate.and(meetingEntity)
//        }

        OrderSpecifier<LocalDateTime> orderBy;

        if (SortOption.LATEST.getValue().equals(sort)) {
            orderBy = meetingEntity.createdAt.desc();
        }
        // 추후 인기순 정렬 로직 추가
        // else if (MeetingSort.POPULAR.getValue().equals(sort)) {
        //     orderBy = ...;
        // }
        else {
            orderBy = meetingEntity.createdAt.desc();
        }

        return queryFactory
                .selectFrom(meetingEntity)
                .where(predicate)
                .orderBy(orderBy)
                .fetch()
                .stream()
                .map(MeetingEntity::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingDTO getMeetingById(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND)).toDTO();
    }
}
