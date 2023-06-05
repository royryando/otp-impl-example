package id.getsabi.service;

import id.getsabi.service.verify.VerihubsWAVerify;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * WhatsApp
 * @url <a href="https://docs.verihubs.com/reference/send_whatsapp_otp_post-1">Docs</a>
 */
public class VerihubsWA implements Runnable {

    @Override
    public void run() {
        try {
            // Set URL
            URL url = new URL("https://api.verihubs.com/v1/whatsapp/otp/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("App-Id", VerihubsSMS.APP_ID);
            connection.setRequestProperty("Api-Key", VerihubsSMS.API_KEY);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Set Body
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msisdn", VerihubsSMS.PHONE);
            jsonObject.put("content", new JSONArray(List.of("Sabi")));
            jsonObject.put("time_limit", 300);
            jsonObject.put("lang_code", "en");
            jsonObject.put("template_name", "send_otp_template");
            jsonObject.put("otp_length", "4");

            // Request
            String jsonBody = jsonObject.toString();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            Thread.sleep(500);
            // Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println("Verihubs\t: WA Response: " + jsonResponse);
            connection.disconnect();

            // Verify OTP
            new VerihubsWAVerify(VerihubsSMS.PHONE, jsonResponse.getString("otp")).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
