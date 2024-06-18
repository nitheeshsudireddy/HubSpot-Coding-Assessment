package com.hubspot.api.dao;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.api.model.CallRecord;


@Repository
public class HubSpotDao {

    
    @Value("${hubspot.api.get.callrecords.url}")
    private String getCallRecordsUrl;

    @Value("${hubspot.api.post.result.url}")
    private String postResultsUrl;


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HubSpotDao(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<CallRecord> fetchCallRecords() {

        String response = restTemplate.getForObject(getCallRecordsUrl, String.class);
        try {
            Map<String, List<CallRecord>> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, List<CallRecord>>>() {});
            return responseMap.get("callRecords");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse call records", e);
        }
    }
}
