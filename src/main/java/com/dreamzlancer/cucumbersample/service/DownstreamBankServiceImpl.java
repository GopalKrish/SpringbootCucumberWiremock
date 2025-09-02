package com.dreamzlancer.cucumbersample.service;

import com.dreamzlancer.cucumbersample.model.UserBankDetailsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DownstreamBankServiceImpl implements DownstreamBankService {

    private final RestTemplate restTemplate;

    @Value("${downstream.bank-service.url}")
    private String bankServiceUrl;

    public DownstreamBankServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserBankDetailsResponse getUserBankDetails(Long userId) {
        String url = bankServiceUrl + "/users/" + userId + "/bank-details";
        log.debug("Calling downstream bank service: {}", url);

        try {
            ResponseEntity<UserBankDetailsResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    UserBankDetailsResponse.class
            );
            return response.getBody();

        } catch (HttpClientErrorException.NotFound ex) {
            log.error("User bank details not found for ID: {}", userId, ex);
            throw new RuntimeException("User bank details not found", ex);
        } catch (ResourceAccessException ex) {
            log.error("Downstream service timeout for user ID: {}", userId, ex);
            throw new RuntimeException("Service timeout", ex);
        } catch (Exception ex) {
            log.error("Unexpected error fetching bank details for user ID: {}", userId, ex);
            throw new RuntimeException("Unexpected error", ex);
        }
    }
}