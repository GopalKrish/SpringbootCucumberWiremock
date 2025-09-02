package com.dreamzlancer.cucumbersample.service;

import com.dreamzlancer.cucumbersample.model.UserBankDetailsResponse;

public interface DownstreamBankService {
    UserBankDetailsResponse getUserBankDetails(Long userId);
}