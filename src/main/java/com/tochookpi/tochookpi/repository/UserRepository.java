package com.tochookpi.tochookpi.repository;

import com.tochookpi.tochookpi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
