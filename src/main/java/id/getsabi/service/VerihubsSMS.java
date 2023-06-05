package id.getsabi.service;

import id.getsabi.service.verify.VerihubsSMSVerify;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * SMS
 * @url <a href="https://docs.verihubs.com/reference/send_otp_post-1">Docs</a>
 */
public class VerihubsSMS implements Runnable {
    public static final String APP_ID = System.getenv("VH_APP_ID");
    public static final String API_KEY = System.getenv("VH_APP_KEY");

    public static final String PHONE = System.getenv("OTP_MSISDN");

    @Override
    public void run() {
        try {
            // Set URL
            URL url = new URL("https://api.verihubs.com/v1/otp/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("App-Id", APP_ID);
            connection.setRequestProperty("Api-Key", API_KEY);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Set Body
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msisdn", PHONE);
            jsonObject.put("template", "This is your OTP $OTP.\nDo not share with anyone.");
            jsonObject.put("time_limit", 300);

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
            System.out.println("Verihubs\t: SMS Response: " + jsonResponse);
            connection.disconnect();

            // Verify OTP
            new VerihubsSMSVerify(PHONE, jsonResponse.getString("otp")).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
