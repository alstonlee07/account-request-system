package com.example.accountrequest.service;

import com.example.accountrequest.dto.AccountRequestResponseDto;
import com.example.accountrequest.dto.CreateAccountRequestDto;
import com.example.accountrequest.entity.AccountRequest;
import com.example.accountrequest.entity.RequestStatus;
import com.example.accountrequest.entity.Role;
import com.example.accountrequest.entity.User;
import com.example.accountrequest.exception.DuplicatePendingRequestException;
import com.example.accountrequest.exception.UnauthorizedActionException;
import com.example.accountrequest.repository.AccountRequestRepository;
import com.example.accountrequest.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRequestServiceTest {

    @Mock
    private AccountRequestRepository accountRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountRequestService accountRequestService;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User("Alice Employee", "alice@example.com", Role.EMPLOYEE);
    }

    @Test
    void createRequest_success_returnsPendingStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(accountRequestRepository.existsByRequesterIdAndSystemNameAndStatus(1L, "VPN Access", RequestStatus.PENDING))
                .thenReturn(false);
        when(accountRequestRepository.save(any(AccountRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountRequestDto dto = new CreateAccountRequestDto();
        dto.setSystemName("VPN Access");
        dto.setReason("需要遠端連線公司內部網路");

        AccountRequestResponseDto result = accountRequestService.createRequest(1L, dto);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(result.getRequesterName()).isEqualTo("Alice Employee");
    }

    @Test
    void createRequest_duplicatePending_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(accountRequestRepository.existsByRequesterIdAndSystemNameAndStatus(1L, "VPN Access", RequestStatus.PENDING))
                .thenReturn(true);

        CreateAccountRequestDto dto = new CreateAccountRequestDto();
        dto.setSystemName("VPN Access");
        dto.setReason("需要遠端連線公司內部網路");

        assertThrows(DuplicatePendingRequestException.class,
                () -> accountRequestService.createRequest(1L, dto));
    }

    @Test
    void approveRequest_byNonManager_throwsUnauthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThrows(UnauthorizedActionException.class,
                () -> accountRequestService.approveRequest(100L, 1L));
    }
}