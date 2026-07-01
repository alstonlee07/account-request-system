package com.example.accountrequest.repository;

import com.example.accountrequest.entity.AccountRequest;
import com.example.accountrequest.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    List<AccountRequest> findByRequesterId(Long requesterId);

    List<AccountRequest> findByRequesterIdAndStatus(Long requesterId, RequestStatus status);

    List<AccountRequest> findByStatus(RequestStatus status);

    boolean existsByRequesterIdAndSystemNameAndStatus(Long requesterId, String systemName, RequestStatus status);
}