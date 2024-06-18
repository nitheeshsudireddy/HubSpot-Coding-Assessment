package com.hubspot.api.model;

public class CallRecord {
    private int customerId;
    private String callId;
    private long startTimestamp;
    
    public CallRecord(int customerId, String callId, long startTimestamp, long endTimestamp) {
        this.customerId = customerId;
        this.callId = callId;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }
    private long endTimestamp;

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public String getCallId() {
        return callId;
    }
    public void setCallId(String callId) {
        this.callId = callId;
    }
    public long getStartTimestamp() {
        return startTimestamp;
    }
    public void setStartTimestamp(long startTimeStamp) {
        this.startTimestamp = startTimeStamp;
    }
    public long getEndTimestamp() {
        return endTimestamp;
    }
    public void setEndTimestamp(long endTimeStamp) {
        this.endTimestamp = endTimeStamp;
    }




}
