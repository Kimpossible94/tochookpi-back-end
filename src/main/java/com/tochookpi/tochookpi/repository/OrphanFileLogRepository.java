package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.OrphanFileLogEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrphanFileLogRepository extends JpaRepository<OrphanFileLogEntity, Long> {}
