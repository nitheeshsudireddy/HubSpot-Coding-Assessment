package com.hubspot.api.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hubspot.api.service.HubSpotApiService;

@RestController
@RequestMapping("/HubSpot/api")
public class HubSpotApiController {
    

    @Autowired
    private HubSpotApiService hubspotApiService;


    @GetMapping("/processCalls")
    public String processCalls(){
        return hubspotApiService.processCalls();
    }
}
