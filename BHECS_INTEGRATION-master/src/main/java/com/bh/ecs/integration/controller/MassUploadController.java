package com.bh.ecs.integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bh.ecs.integration.service.MassUploadService;

@RequestMapping(value="/massupload")
@RestController
public class MassUploadController {
	
	@Autowired
	MassUploadService massUploadService;
	
	
	@GetMapping("/validateRules")
    @Scheduled(cron = "0 */15 * * * *")//Every 15minutes 
    public void validateRulesService() {
		massUploadService.validateRulesService();
    }
	@GetMapping("/pendingQueue")
	@Scheduled(cron = "0 */10 * * * *")//Every 10 minutes
	public void approvedQueue() {
		massUploadService.approvedQueue();
	}
}