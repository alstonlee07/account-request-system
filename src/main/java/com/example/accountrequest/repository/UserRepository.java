package com.example.accountrequest.repository;

import com.example.accountrequest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}