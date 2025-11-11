package com.example.webhookapp.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void executeFlow() {
        try {
            // Step 1: Generate webhook details
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            ResponseEntity<Map> response = null;


            for (int i = 1; i <= 3; i++) {
                try {
                    response = restTemplate.postForEntity(url, requestBody, Map.class);
                    break; // Success
                } catch (HttpServerErrorException e) {
                    System.err.println("Attempt " + i + " failed with 500: " + e.getResponseBodyAsString());
                    if (i == 3) throw e;
                    Thread.sleep(2000);
                }
            }

            if (response == null || response.getBody() == null) {
                System.err.println("No response received from the server.");
                return;
            }


            System.out.println("Raw Response Body: " + response.getBody());
            System.out.println("Raw Body Keys: " + response.getBody().keySet());

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            if (webhookUrl == null || webhookUrl.endsWith("/JAVA")) {
                System.err.println("⚠️  Warning: Invalid webhook URL detected. The remote server might be down or not returning data correctly.");
                return;
            }


            String finalQuery = "SELECT * FROM students;"; // You can replace with your actual SQL

            Map<String, String> answerBody = new HashMap<>();
            answerBody.put("finalQuery", finalQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(answerBody, headers);

            System.out.println("Submitting SQL query...");

            ResponseEntity<String> finalResponse = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("Submission Response: " + finalResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}