package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingParticipantEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipantEntity, Long> {
    Optional<MeetingParticipantEntity> findByMeetingAndUser(MeetingEntity meetingEntity, UserEntity userEntity);

}
