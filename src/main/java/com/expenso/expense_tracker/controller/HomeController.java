package com.expenso.expense_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "Welcome to the Expense Tracker API!";
	}

	@GetMapping("/health")
	public String healthCheck() {
        return "API is running healthy!";
	}

	@GetMapping("/status")
	public String status() {
        return "API is operational!";
	}

	@GetMapping("/version")
	public String version() {
        return "Expense Tracker API Version 1.0";
	}
}