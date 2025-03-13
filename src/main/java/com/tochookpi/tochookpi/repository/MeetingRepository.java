package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {
    Optional<List<MeetingEntity>> findByOrganizerId(Long id);
}
