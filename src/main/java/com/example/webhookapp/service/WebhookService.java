package com.example.webhookapp.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Service
public class WebhookService {

    public void executeFlow() {
        System.out.println("🚀 Starting Webhook Flow...");

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        System.out.println("🔹 Requesting webhook details from: " + url);

        try {
            RestTemplate restTemplate = new RestTemplate();

            // ✅ Send an empty JSON body
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>("{}", headers);

            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            System.out.println("✅ Raw Response Body: " + response.getBody());

            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                String webhookUrl = (String) responseBody.get("webhook");
                String accessToken = (String) responseBody.get("accessToken");

                System.out.println("Webhook URL: " + webhookUrl);
                System.out.println("Access Token: " + accessToken);

                if (webhookUrl != null && accessToken != null) {
                    sendSQLQuery(webhookUrl, accessToken);
                } else {
                    System.err.println("⚠️ Invalid response. Missing webhook or accessToken.");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendSQLQuery(String webhookUrl, String accessToken) {
        System.out.println("📡 Submitting SQL Query to: " + webhookUrl);

        // The SQL query required by the PDF
        String sqlQuery =
                "SELECT department, COUNT(*) AS employee_count " +
                        "FROM employee " +
                        "GROUP BY department " +
                        "ORDER BY employee_count DESC " +
                        "LIMIT 1;";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("query", sqlQuery);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
            System.out.println("✅ SQL Query Response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Failed to send SQL Query: " + e.getMessage());
        }
    }
}