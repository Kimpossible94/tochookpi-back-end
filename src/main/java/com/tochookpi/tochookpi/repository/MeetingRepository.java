package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
