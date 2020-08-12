package com.zoidbergv.telegrambot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zoidbergv.telegrambot.report.ReportService;

@RestController
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping(path = "/sendReport", produces = MediaType.TEXT_PLAIN_VALUE)
	public String sendReport() {
		reportService.sendReport();
		return "Done";
	}

}
