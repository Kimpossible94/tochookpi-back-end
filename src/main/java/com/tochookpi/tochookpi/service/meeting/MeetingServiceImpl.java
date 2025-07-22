package com.tochookpi.tochookpi.service.meeting;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tochookpi.tochookpi.dto.meeting.MeetingDTO;
import com.tochookpi.tochookpi.entity.*;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.MeetingCategory;
import com.tochookpi.tochookpi.enums.MeetingStatus;
import com.tochookpi.tochookpi.enums.SortOption;
import com.tochookpi.tochookpi.exception.S3OrphanFileException;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingParticipantRepository;
import com.tochookpi.tochookpi.repository.MeetingRepository;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final S3Service s3Service;
    private final JPAQueryFactory queryFactory;

    public MeetingServiceImpl(UserRepository userRepository,
                              MeetingRepository meetingRepository,
                              MeetingParticipantRepository meetingParticipantRepository,
                              S3Service s3Service,
                              JPAQueryFactory queryFactory) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.s3Service = s3Service;
        this.queryFactory = queryFactory;
    }

    @Override
    public void createMeeting(String loggedInUserEmail, MultipartFile image, MeetingDTO meetingDTO) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingDTO.toEntity(userEntity);

        // 스케쥴 추가
        List<MeetingScheduleEntity> scheduleEntities = meetingDTO.toScheduleEntities(meetingEntity);
        meetingEntity.setSchedules(scheduleEntities);

        // 모임 생성한 사용자도 참가자에 추가
        ArrayList<MeetingParticipantEntity> meetingScheduleEntities = new ArrayList<>();
        MeetingParticipantEntity meetingParticipantEntity = new MeetingParticipantEntity();
        meetingParticipantEntity.setMeeting(meetingEntity);
        meetingParticipantEntity.setUser(userEntity);
        meetingScheduleEntities.add(meetingParticipantEntity);
        meetingEntity.setParticipants(meetingScheduleEntities);

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
    public List<MeetingDTO> getMeetings(String searchTerm, List<MeetingCategory> category, String sort) {
        QMeetingEntity meetingEntity = QMeetingEntity.meetingEntity;
        BooleanBuilder predicate = new BooleanBuilder();

        if(searchTerm != null && !searchTerm.isEmpty()) {
            predicate.and(meetingEntity.title.containsIgnoreCase(searchTerm));
        }

        if(category != null && !category.isEmpty()) {
            predicate.and(meetingEntity.category.in(category));
        }

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
    public MeetingDTO getMeetingById(String loggedInUserEmail, Long id) {
        MeetingDTO meetingDTO = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND)).toDTO();

        boolean isParticipating = meetingDTO.getParticipants().stream()
                .anyMatch(participant -> participant.getEmail().equals(loggedInUserEmail));

        meetingDTO.setParticipating(isParticipating);

        return meetingDTO;
    }

    @Override
    public void joinMeeting(String loggedInUserEmail, Long id) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(MeetingStatus.ENDED.equals(meetingEntity.getStatus()))
            throw new TochookpiException(ErrorCode.MEETING_ALREADY_ENDED);

        boolean isJoin = meetingEntity.getParticipants().stream()
                .anyMatch(p -> p.getUser().getEmail().equals(loggedInUserEmail));

        if(isJoin) throw new TochookpiException(ErrorCode.MEETING_ALREADY_JOINED);

        if(meetingEntity.getCurrentParticipantsCnt() >= meetingEntity.getMaxParticipantsCnt())
            throw new TochookpiException(ErrorCode.MEETING_FULL);

        MeetingParticipantEntity meetingParticipantEntity = new MeetingParticipantEntity();
        meetingParticipantEntity.setMeeting(meetingEntity);
        meetingParticipantEntity.setUser(userEntity);
        meetingEntity.getParticipants().add(meetingParticipantEntity);

        meetingEntity.setCurrentParticipantsCnt(meetingEntity.getCurrentParticipantsCnt() + 1);

        meetingRepository.save(meetingEntity);
    }

    @Override
    public void leaveMeeting(String loggedInUserEmail, Long id) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(MeetingStatus.ENDED.equals(meetingEntity.getStatus()))
            throw new TochookpiException(ErrorCode.MEETING_ALREADY_ENDED);

        if(meetingEntity.getOrganizer().equals(userEntity))
            throw new TochookpiException(ErrorCode.MEETING_ORGANIZER_CANNOT_LEAVE);

        MeetingParticipantEntity meetingParticipantEntity = meetingParticipantRepository
                .findByMeetingAndUser(meetingEntity, userEntity)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_JOINED));
        meetingParticipantRepository.delete(meetingParticipantEntity);

        meetingEntity.setCurrentParticipantsCnt(meetingEntity.getCurrentParticipantsCnt() - 1);

        meetingRepository.save(meetingEntity);
    }

    @Override
    public void deleteMeeting(String loggedInUserEmail, Long id) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(!meetingEntity.getOrganizer().equals(userEntity)) throw new TochookpiException(ErrorCode.MEETING_ACCESS_DENIED);

        meetingRepository.delete(meetingEntity);
    }
}
