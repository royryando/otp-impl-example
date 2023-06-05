package id.getsabi.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Flash Call
 * @url <a href="https://docs.verihubs.com/reference/send_flashcall_post">Docs</a>
 */
public class VerihubsFC implements Runnable {

    @Override
    public void run() {
        try {
            // Set URL
            URL url = new URL("https://api.verihubs.com/v2/flashcall/send");
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
            jsonObject.put("time_limit", 300);
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
            System.out.println("Verihubs\t: FC Response: " + jsonResponse);
            connection.disconnect();

            // Verify OTP (skipped due to no otp response available from API)
            //new VerihubsWAVerify(VerihubsSMS.PHONE, jsonResponse.getString("otp")).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
