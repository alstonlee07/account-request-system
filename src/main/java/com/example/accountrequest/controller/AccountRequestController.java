package com.example.accountrequest.controller;

import com.example.accountrequest.dto.AccountRequestResponseDto;
import com.example.accountrequest.dto.CreateAccountRequestDto;
import com.example.accountrequest.dto.RejectRequestDto;
import com.example.accountrequest.entity.RequestStatus;
import com.example.accountrequest.service.AccountRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class AccountRequestController {

    private final AccountRequestService accountRequestService;

    public AccountRequestController(AccountRequestService accountRequestService) {
        this.accountRequestService = accountRequestService;
    }

    @PostMapping
    public ResponseEntity<AccountRequestResponseDto> createRequest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAccountRequestDto dto) {

        AccountRequestResponseDto response = accountRequestService.createRequest(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<AccountRequestResponseDto> listRequests(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) RequestStatus status) {

        return accountRequestService.listRequests(userId, status);
    }

    @GetMapping("/{id}")
    public AccountRequestResponseDto getRequestById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return accountRequestService.getRequestById(id, userId);
    }

    @PatchMapping("/{id}/approve")
    public AccountRequestResponseDto approveRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return accountRequestService.approveRequest(id, userId);
    }

    @PatchMapping("/{id}/reject")
    public AccountRequestResponseDto rejectRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RejectRequestDto dto) {

        return accountRequestService.rejectRequest(id, userId, dto);
    }

    @PatchMapping("/{id}/cancel")
    public AccountRequestResponseDto cancelRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return accountRequestService.cancelRequest(id, userId);
    }
}