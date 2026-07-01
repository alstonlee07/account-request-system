package com.example.accountrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectRequestDto {

    @NotBlank(message = "拒絕原因不可為空")
    @Size(max = 500, message = "拒絕原因長度不可超過 500 字")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}