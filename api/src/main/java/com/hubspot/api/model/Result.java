package com.hubspot.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    @JsonProperty("customerId")
    private int customerId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("maxConcurrentCalls")
    private int maxConcurrentCalls;
    @JsonProperty("callIds")
    private List<String> callIds;
    @JsonProperty("timestamp")
    private long timestamp;
    
    public Result(int customerId, String date, int maxConcurrentCalls, List<String> callIds, long timestamp ) {
        this.customerId = customerId;
        this.date = date;
        this.maxConcurrentCalls = maxConcurrentCalls;
        this.callIds = callIds;
        this.timestamp = timestamp;
    }

    public int getCustomerId() {
        return customerId;
    }


    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getCallIds() {
        return callIds;
    }

    public void setCallIds(List<String> callIds) {
        this.callIds = callIds;
    }

}
