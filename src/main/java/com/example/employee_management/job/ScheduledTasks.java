package com.example.employee_management.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 15000)
    public void scheduleTask() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String strDate = dateFormat.format(new Date());

        System.out.println("Fixed Rate Scheduler: Task running at - " + strDate);

        // Call free APIs
        callFreeAPIs();
    }

    private void callFreeAPIs() {
        try {
            // 1. JSONPlaceholder API (Free REST API)
            String jsonPlaceholderUrl = "https://jsonplaceholder.typicode.com/posts/1";
            String jsonResponse = restTemplate.getForObject(jsonPlaceholderUrl, String.class);
            System.out.println("📡 JSONPlaceholder API - Response received: " + jsonResponse);

            // 2. Random User API (Free)
            String randomUserUrl = "https://randomuser.me/api/";
            String userResponse = restTemplate.getForObject(randomUserUrl, String.class);
            System.out.println("👤 Random User API - Called successfully, response: " + userResponse);

            // 3. Cat Facts API (Free)
            String catFactsUrl = "https://catfact.ninja/fact";
            String catResponse = restTemplate.getForObject(catFactsUrl, String.class);
            System.out.println("🐱 Cat Facts API - Fact retrieved: " + catResponse);

            System.out.println("✅ All free APIs called successfully!");
            System.out.println("---");

        } catch (RestClientException e) {
            System.err.println("❌ Error calling free APIs: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in scheduled task: " + e.getMessage());
        }
    }
}