package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.MeetingParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipantEntity, Long> {
}
