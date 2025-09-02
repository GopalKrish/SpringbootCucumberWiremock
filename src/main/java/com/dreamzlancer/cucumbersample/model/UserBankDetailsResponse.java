package com.dreamzlancer.cucumbersample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBankDetailsResponse {
    private Long userId;
    private String accountNumber;
    private String bankName;
    private String branchCode;
    private String accountType;
    private String ifscCode;
    private LocalDateTime lastUpdated;
}