package com.hubspot.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hubspot.api.dao.HubSpotDao;
import com.hubspot.api.model.CallRecord;
import com.hubspot.api.model.Result;
import com.hubspot.api.model.Call;


@Service
public class HubSpotApiService {
    
    @Autowired
    private HubSpotDao hubspotDao;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${hubspot.api.post.result.url}")
    private String postResultsUrl;

    public String processCalls() {
        List<CallRecord> callRecords = hubspotDao.fetchCallRecords();
        List<CallRecord> expandedCallRecords = expandCallRecords(callRecords);
        Map<Integer, Map<LocalDate, List<CallRecord>>> groupedRecords = groupCallRecords(expandedCallRecords);
        List<Result> results = calculateMaxConcurrentCalls(groupedRecords);
        return postResults(results);
    }

    private List<CallRecord> expandCallRecords(List<CallRecord> callRecords) {
        List<CallRecord> expandedRecords = new ArrayList<>();
        for (CallRecord record : callRecords) {
            long start = record.getStartTimestamp();
            long end = record.getEndTimestamp();
            LocalDate startDate = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate();

            if (!startDate.equals(endDate)) {

                long endOfDay = startDate.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC) * 1000 - 1;
                expandedRecords.add(new CallRecord(record.getCustomerId(), record.getCallId(), start, endOfDay));
                start = endOfDay + 1;

                while (start < end) {
                    startDate = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toLocalDate();
                    endOfDay = startDate.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC) * 1000 - 1;
                    if (end > endOfDay) {
                        expandedRecords.add(new CallRecord(record.getCustomerId(), record.getCallId(), start, endOfDay));
                        start = endOfDay + 1;
                    } else {
                        expandedRecords.add(new CallRecord(record.getCustomerId(), record.getCallId(), start, end));
                        break;
                    }
                }
            } else {
                expandedRecords.add(record);
            }
        }
        return expandedRecords;
    }

    private Map<Integer, Map<LocalDate, List<CallRecord>>> groupCallRecords(List<CallRecord> callRecords) {
        Map<Integer, Map<LocalDate, List<CallRecord>>> customerCalls = new HashMap<>();
        for (CallRecord record : callRecords) {
            int customerId = record.getCustomerId();
            LocalDate date = Instant.ofEpochMilli(record.getStartTimestamp()).atZone(ZoneOffset.UTC).toLocalDate();
            customerCalls.computeIfAbsent(customerId, k -> new HashMap<>())
                         .computeIfAbsent(date, k -> new ArrayList<>())
                         .add(record);
        }
        return customerCalls;
    }

    private List<Result> calculateMaxConcurrentCalls(Map<Integer, Map<LocalDate, List<CallRecord>>> customerCalls) {
        List<Result> results = new ArrayList<>();
        for (Map.Entry<Integer, Map<LocalDate, List<CallRecord>>> customerEntry : customerCalls.entrySet()) {
            int customerId = customerEntry.getKey();

            Map<LocalDate, List<CallRecord>> sortedDateCalls = new TreeMap<>(customerEntry.getValue());

            for (Map.Entry<LocalDate, List<CallRecord>> dateEntry : sortedDateCalls.entrySet()) {
                LocalDate date = dateEntry.getKey();
                List<CallRecord> callRecords = dateEntry.getValue();
                List<Call> calls = new ArrayList<>();

                for (CallRecord call : callRecords) {
                    calls.add(new Call(call.getStartTimestamp(), 1, call.getCallId(), call.getStartTimestamp(), call.getEndTimestamp()));
                    calls.add(new Call(call.getEndTimestamp(), -1, call.getCallId(), call.getStartTimestamp(), call.getEndTimestamp()));
                }

                calls.sort(Comparator.comparing(Call::getTimestamp).thenComparing(Call::getType));

                int maxConcurrentCalls = 0;
                int currentConcurrentCalls = 0;
                long maxTimestamp = 0;
                Set<String> activeCalls = new HashSet<>();
                List<String> maxCallIds = new ArrayList<>();

                for (Call call : calls) {
                    if (call.getType() == 1) {
                        currentConcurrentCalls++;
                        activeCalls.add(call.getCallId());

                        if (currentConcurrentCalls > maxConcurrentCalls) {
                            maxConcurrentCalls = currentConcurrentCalls;
                            maxTimestamp = call.getTimestamp();
                            maxCallIds = new ArrayList<>(activeCalls);
                        }
                    } else {
                        currentConcurrentCalls--;
                        activeCalls.remove(call.getCallId());
                    }
                }

                Collections.sort(maxCallIds); 
                
                Result result = new Result(customerId, date.toString(), maxConcurrentCalls, maxCallIds, maxTimestamp);
                results.add(result);
            }
        }
        System.out.println(results);
        return results;
    }

    private String postResults(List<Result> results) {
        String response;

        try{
            Map<String, List<Result>> requestBody = new HashMap<>();
            requestBody.put("results", results);
                
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(requestBody);

            System.out.println(json);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
        
            ResponseEntity<String> result = restTemplate.postForEntity(postResultsUrl, entity, String.class);

            response = result.getStatusCode().toString();
        }
        catch (HttpClientErrorException ex) {
            System.out.println("Exception status code: " + ex.getStatusCode());
            System.out.println("Exception response body: " + ex.getResponseBodyAsString());
            System.out.println("Exception during send invitations post request: " + ex.getMessage());
            response = ex.getResponseBodyAsString();
        }
        
        return response;
    }

}
