package com.tochookpi.tochookpi.service.meeting;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tochookpi.tochookpi.dto.meeting.LocationDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingRequestDTO;
import com.tochookpi.tochookpi.dto.meeting.MeetingResponseDTO;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantService meetingParticipantService;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final S3Service s3Service;
    private final JPAQueryFactory queryFactory;

    @Override
    public void createMeeting(String loggedInUserId, MultipartFile image, MeetingRequestDTO meetingDTO) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingDTO.toEntity(userEntity);
        setMeetingParticipant(userEntity, meetingEntity, meetingDTO.getParticipants());
        saveMeeting(image, meetingEntity);
    }

    @Override
    public List<MeetingResponseDTO> getMeetings(String loggedInUserId, String searchTerm, List<MeetingCategory> category, String sort, String type) {
        QMeetingEntity meeting = QMeetingEntity.meetingEntity;
        QMeetingParticipantEntity participant = QMeetingParticipantEntity.meetingParticipantEntity;
        BooleanBuilder predicate = new BooleanBuilder();

        // 검색어
        if(searchTerm != null && !searchTerm.equals("")) {
            predicate.and(meeting.title.containsIgnoreCase(searchTerm));
        }

        // 카테고리
        if(category != null && !category.isEmpty()) {
            predicate.and(meeting.category.in(category));
        }

        // 모임 타입(생성한 또는 참여한)
        if(type != null && !type.equals("")) {
            UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                    .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

            if("created".equals(type)) {
                predicate.and(meeting.organizer.eq(userEntity));
            } else if ("joined".equals(type)) {
                predicate.and(meeting.organizer.ne(userEntity));

                BooleanExpression joinedPredicate = JPAExpressions
                        .selectOne()
                        .from(participant)
                        .where(participant.meeting.eq(meeting)
                                .and(participant.user.eq(userEntity)))
                        .exists();
                predicate = predicate.and(joinedPredicate);

            }
        }

        // 정렬기준 설정
        OrderSpecifier<LocalDateTime> orderBy;

        if (SortOption.LATEST.getValue().equals(sort)) {
            orderBy = meeting.createdAt.desc();
        }
        // 추후 인기순 정렬 로직 추가
//        else if (SortOption.POPULAR.getValue().equals(sort)) {
//            orderBy = ...;
//        }
        else {
            orderBy = meeting.createdAt.desc();
        }

        return queryFactory
                .selectFrom(meeting)
                .where(predicate)
                .orderBy(orderBy)
                .fetch()
                .stream()
                .map(MeetingEntity::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MeetingResponseDTO getMeetingById(String loggedInUserId, Long id) {
        MeetingResponseDTO meetingDTO = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND)).toDTO();

        boolean isParticipating = meetingDTO.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(Long.parseLong(loggedInUserId)));

        meetingDTO.setParticipating(isParticipating);

        return meetingDTO;
    }

    @Override
    public void modifyMeeting(String loggedInUserId, Long id, MultipartFile image, MeetingRequestDTO newMeeting) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(!meetingEntity.getOrganizer().getId().equals(loggedInUserId)) new TochookpiException(ErrorCode.MEETING_ONLY_ORGANIZER);

        meetingEntity.setTitle(newMeeting.getTitle());
        meetingEntity.setDescription(newMeeting.getDescription());
        meetingEntity.setCategory(newMeeting.getCategory());
        meetingEntity.setStartDate(newMeeting.getStartDate());
        meetingEntity.setEndDate(newMeeting.getEndDate());
        meetingEntity.setLocation(newMeeting.getLocation().toLocation());

        setMeetingParticipant(userEntity, meetingEntity, newMeeting.getParticipants());
        saveMeeting(image, meetingEntity);
    }

    private void setMeetingParticipant(UserEntity userEntity, MeetingEntity meetingEntity, List<Long> participants) {
        List<MeetingParticipantEntity> meetingParticipantEntities =
                meetingParticipantService.convertUserIdsToMeetingParticipantEntities(participants, meetingEntity);
        MeetingParticipantEntity meetingParticipantEntity = new MeetingParticipantEntity();
        meetingParticipantEntity.setMeeting(meetingEntity);
        meetingParticipantEntity.setUser(userEntity);
        meetingParticipantEntities.add(meetingParticipantEntity);

        meetingEntity.getParticipants().clear();
        meetingEntity.getParticipants().addAll(meetingParticipantEntities);
    }

    private void saveMeeting(MultipartFile image, MeetingEntity meetingEntity) {
        String imageUrl = null;
        if(image != null) {
            imageUrl = s3Service.uploadFile(image, "meeting");
            meetingEntity.setImage(imageUrl);
        }

        try {
            meetingRepository.save(meetingEntity);
        } catch (Exception e) {
            e.printStackTrace();
            if (imageUrl != null) {
                throw new S3OrphanFileException(ErrorCode.MEETING_FAIL_SAVE_MEETING, imageUrl);
            }
            throw new TochookpiException(ErrorCode.MEETING_FAIL_SAVE_MEETING);
        }
    }

    @Override
    public void joinMeeting(String loggedInUserId, Long id) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(MeetingStatus.ENDED.equals(meetingEntity.getStatus()))
            throw new TochookpiException(ErrorCode.MEETING_ALREADY_ENDED);

        boolean isJoin = meetingEntity.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(Long.parseLong(loggedInUserId)));

        if(isJoin) throw new TochookpiException(ErrorCode.MEETING_ALREADY_JOINED);

        MeetingParticipantEntity meetingParticipantEntity = new MeetingParticipantEntity();
        meetingParticipantEntity.setMeeting(meetingEntity);
        meetingParticipantEntity.setUser(userEntity);
        meetingEntity.getParticipants().add(meetingParticipantEntity);

        meetingRepository.save(meetingEntity);
    }

    @Override
    public void leaveMeeting(String loggedInUserId, Long id) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
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

        meetingRepository.save(meetingEntity);
    }

    @Override
    public void deleteMeeting(String loggedInUserId, Long id) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        MeetingEntity meetingEntity = meetingRepository.findById(id)
                .orElseThrow(() -> new TochookpiException(ErrorCode.MEETING_NOT_FOUND));

        if(!meetingEntity.getOrganizer().equals(userEntity)) throw new TochookpiException(ErrorCode.MEETING_ACCESS_DENIED);

        meetingRepository.delete(meetingEntity);
    }
}
