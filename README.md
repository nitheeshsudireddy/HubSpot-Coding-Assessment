# HubSpot Coding Assessment

## Problem
Many companies use HubSpot for its calling capabilities. Sales reps use the HubSpot product throughout the day to make phone calls to prospects.

We've found that certain customers using HubSpot have a large number of sales reps concurrently making calls with HubSpot, and this puts heavy load on our systems. In response to this, we'd like to bill our customers based on their peak calling load. In other words, we'd like to bill customers based on their maximum number of concurrent calls.

You're provided with an HTTP GET endpoint that returns phone call records represented as JSON: https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=d03c63472a401b9c68b3c1e51eeb

### Each call looks something like this:
```
{
  "customerId": 123,
  "callId": "2c269d25-deb9-42cf-927c-543112f7a76b",
  "startTimestamp": 1707314726000,
  "endTimestamp": 1707317769000
}
```

* **customerId:** is a unique identifier for one customer. One customer may have many sales reps concurrently making calls.
* **callId:** is a unique identifier for a single phone call. No two phone calls will have the same callId.
* **startTimestamp:** is when the call started. This value is given as a UNIX timestamp in milliseconds. In other words, it's the number of milliseconds that passed between the UNIX epoch (1970-01-01 00:00:00 UTC) and the start of the call.
* **endTimestamp:** is when the call ended. This value is also given as a UNIX timestamp in milliseconds. endTimestamp will always be greater than startTimestamp for a given call record.

For the billing team to charge our customers correctly, they need to know the maximum number of concurrent calls for each customer for each day. The billing team has asked you to POST this information to the following endpoint: https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=d03c63472a401b9c68b3c1e51eeb. The POST body must be in the following format:
```
{
  "results": [
    {
      "customerId": 123,
      "date": "2024-02-07",
      "maxConcurrentCalls": 1,
      "timestamp": 1707314726000,
      "callIds": [
        "2c269d25-deb9-42cf-927c-543112f7a76b"
      ]
    }
  ]
}
```

* ```date``` is a UTC date in the format YYYY-MM-DD. So the example date refers to February 7th 2024.
* ```maxConcurrentCalls``` is the maximum number of simultaneous calls that occurred at any time during the corresponding date for this customer.
* ```timestamp``` is a UNIX timestamp (in milliseconds) at which maxConcurrentCalls was reached for this customer and date. There could be multiple time periods during this date where maxConcurrentCalls is reached. A timestamp during any of these time periods can be chosen.
* ```callIds``` is an array of calls that were happening for this customer at timestamp. The length of this array should equal maxConcurrentCalls. The order of callIds does not matter.

This example response only has one entry in the results array, but we expect the actual answer to have multiple results.

### Note
* The ```startTimestamp``` of a call is inclusive, and the endTimestamp of a call is exclusive. This means that:
  * If call A has an endTimestamp of 123, and call B has a startTimestamp of 123, they never overlapped.
  * For a given results entry, the timestamp should always be less than the endTimestamp of each call in callIds.
* A single call may span multiple UTC dates, and calls can be arbitrarily long.
* The order of results posted does not matter.
* For a given customerId and date, if no phone calls occurred during that date, there should be no results entry with that customerId and date combination.
* No two entries in the results array should have identical values for both customerId and date.
* In other words, for a given customerId and date combination, there should be at most one entry in the array.


## Setup
Load this project in VSCode from Git, or download and open as JAVA project Run the application and visit http://localhost:8080/HubSpot/api/processCalls Response: 200 OK
