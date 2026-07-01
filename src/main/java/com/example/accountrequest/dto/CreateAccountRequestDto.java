package com.example.accountrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAccountRequestDto {

    @NotBlank(message = "系統名稱不可為空")
    @Size(max = 100, message = "系統名稱長度不可超過 100 字")
    private String systemName;

    @NotBlank(message = "申請原因不可為空")
    @Size(max = 500, message = "申請原因長度不可超過 500 字")
    private String reason;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}