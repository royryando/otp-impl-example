package id.getsabi.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
 * https://nusasms.com/sms-api/
 */
public class NusaSMS implements Runnable {


    @Override
    public void run() {
        try {
            // Set URL
            String baseUrl = "https://api.nusasms.com/api/v3/sendsms/plain";
            Map<String, String> parameters = new HashMap<>();
            parameters.put("user", System.getenv("NUSA_USER"));
            parameters.put("password", System.getenv("NUSA_PASS"));
            parameters.put("SMSText", "Kode OTP Anda 123456");
            parameters.put("GSM", System.getenv("OTP_MSISDN"));
            parameters.put("otp", "Y");
            parameters.put("output", "json");
            StringBuilder urlBuilder = new StringBuilder(baseUrl);

            // Put parameters
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                String encodedValue = URLEncoder.encode(paramValue, StandardCharsets.UTF_8);
                urlBuilder.append(urlBuilder.toString().contains("?") ? "&" : "?");
                urlBuilder.append(paramName).append("=").append(encodedValue);
            }

            // Request
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println("NusaSMS\t\t: SMS Response: " + jsonResponse);
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
