package com.sensorweb.datacenterlaadsservice.controller;

import com.sensorweb.datacenterlaadsservice.service.InsertLAADSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LAADSController {
    @Autowired
    private InsertLAADSService insertLaadsService;

}
