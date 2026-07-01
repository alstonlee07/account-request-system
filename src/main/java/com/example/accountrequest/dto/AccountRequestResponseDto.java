package com.example.accountrequest.dto;

import com.example.accountrequest.entity.RequestStatus;

import java.time.LocalDateTime;

public class AccountRequestResponseDto {

    private final Long id;
    private final String requesterName;
    private final String systemName;
    private final String reason;
    private final RequestStatus status;
    private final String reviewedByName;
    private final String reviewComment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime reviewedAt;

    public AccountRequestResponseDto(Long id, String requesterName, String systemName, String reason,
                                      RequestStatus status, String reviewedByName, String reviewComment,
                                      LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime reviewedAt) {
        this.id = id;
        this.requesterName = requesterName;
        this.systemName = systemName;
        this.reason = reason;
        this.status = status;
        this.reviewedByName = reviewedByName;
        this.reviewComment = reviewComment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewedAt = reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getReason() {
        return reason;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}