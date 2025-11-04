package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingReviewRepository extends JpaRepository<MeetingReviewEntity, Long> {
    List<MeetingReviewEntity> findByMeeting(MeetingEntity meeting);
}
