package com.example.accountrequest.service;

import com.example.accountrequest.dto.AccountRequestResponseDto;
import com.example.accountrequest.dto.CreateAccountRequestDto;
import com.example.accountrequest.dto.RejectRequestDto;
import com.example.accountrequest.entity.AccountRequest;
import com.example.accountrequest.entity.RequestStatus;
import com.example.accountrequest.entity.Role;
import com.example.accountrequest.entity.User;
import com.example.accountrequest.exception.DuplicatePendingRequestException;
import com.example.accountrequest.exception.InvalidRequestStateException;
import com.example.accountrequest.exception.ResourceNotFoundException;
import com.example.accountrequest.exception.UnauthorizedActionException;
import com.example.accountrequest.repository.AccountRequestRepository;
import com.example.accountrequest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountRequestService {

    private final AccountRequestRepository accountRequestRepository;
    private final UserRepository userRepository;

    public AccountRequestService(AccountRequestRepository accountRequestRepository,
                                  UserRepository userRepository) {
        this.accountRequestRepository = accountRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AccountRequestResponseDto createRequest(Long requesterId, CreateAccountRequestDto dto) {
        User requester = getUserOrThrow(requesterId);

        boolean hasDuplicatePending = accountRequestRepository.existsByRequesterIdAndSystemNameAndStatus(
                requesterId, dto.getSystemName(), RequestStatus.PENDING);

        if (hasDuplicatePending) {
            throw new DuplicatePendingRequestException(
                    "你已經有一筆關於「" + dto.getSystemName() + "」尚未審核的申請,請等待審核結果");
        }

        AccountRequest request = new AccountRequest(requester, dto.getSystemName(), dto.getReason());
        AccountRequest saved = accountRequestRepository.save(request);

        return toResponseDto(saved);
    }

    @Transactional
    public AccountRequestResponseDto approveRequest(Long requestId, Long managerId) {
        User manager = getUserOrThrow(managerId);
        requireManagerRole(manager);

        AccountRequest request = getRequestOrThrow(requestId);
        requirePendingStatus(request);

        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedBy(manager);
        request.setReviewedAt(LocalDateTime.now());

        return toResponseDto(request);
    }

    @Transactional
    public AccountRequestResponseDto rejectRequest(Long requestId, Long managerId, RejectRequestDto dto) {
        User manager = getUserOrThrow(managerId);
        requireManagerRole(manager);

        AccountRequest request = getRequestOrThrow(requestId);
        requirePendingStatus(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedBy(manager);
        request.setReviewComment(dto.getComment());
        request.setReviewedAt(LocalDateTime.now());

        return toResponseDto(request);
    }

    @Transactional
    public AccountRequestResponseDto cancelRequest(Long requestId, Long requesterId) {
        getUserOrThrow(requesterId);

        AccountRequest request = getRequestOrThrow(requestId);

        if (!request.getRequester().getId().equals(requesterId)) {
            throw new UnauthorizedActionException("你只能取消自己送出的申請");
        }

        requirePendingStatus(request);

        request.setStatus(RequestStatus.CANCELLED);
        request.setReviewedAt(LocalDateTime.now());

        return toResponseDto(request);
    }
        public AccountRequestResponseDto getRequestById(Long requestId, Long requestingUserId) {
        User user = getUserOrThrow(requestingUserId);
        AccountRequest request = getRequestOrThrow(requestId);

        if (user.getRole() == Role.EMPLOYEE && !request.getRequester().getId().equals(requestingUserId)) {
            throw new UnauthorizedActionException("你沒有權限查看這筆申請");
        }

        return toResponseDto(request);
    }

    public List<AccountRequestResponseDto> listRequests(Long requestingUserId, RequestStatus statusFilter) {
        User user = getUserOrThrow(requestingUserId);

        List<AccountRequest> requests;
        if (user.getRole() == Role.MANAGER) {
            requests = (statusFilter != null)
                    ? accountRequestRepository.findByStatus(statusFilter)
                    : accountRequestRepository.findAll();
        } else {
            requests = (statusFilter != null)
                    ? accountRequestRepository.findByRequesterIdAndStatus(requestingUserId, statusFilter)
                    : accountRequestRepository.findByRequesterId(requestingUserId);
        }

        return requests.stream()
                .map(this::toResponseDto)
                .toList();
    }
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + userId + " 的使用者"));
    }

    private AccountRequest getRequestOrThrow(Long requestId) {
        return accountRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + requestId + " 的申請"));
    }

    private void requireManagerRole(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("只有 MANAGER 角色可以執行這個操作");
        }
    }

    private void requirePendingStatus(AccountRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestStateException(
                    "這筆申請目前狀態是 " + request.getStatus() + ",無法再變更狀態");
        }
    }

    private AccountRequestResponseDto toResponseDto(AccountRequest request) {
        User reviewer = request.getReviewedBy();

        return new AccountRequestResponseDto(
                request.getId(),
                request.getRequester().getName(),
                request.getSystemName(),
                request.getReason(),
                request.getStatus(),
                reviewer != null ? reviewer.getName() : null,
                request.getReviewComment(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getReviewedAt()
        );
    }
}