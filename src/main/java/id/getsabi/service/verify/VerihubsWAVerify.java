package id.getsabi.service.verify;

import id.getsabi.service.VerihubsSMS;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VerihubsWAVerify implements Runnable {
    private final String phoneNumber;

    private final String otp;

    public VerihubsWAVerify(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }

    @Override
    public void run() {
        try {
            // Set URL
            URL url = new URL("https://api.verihubs.com/v1/whatsapp/otp/verify");
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
            jsonObject.put("msisdn", this.phoneNumber);
            jsonObject.put("otp", this.otp);
            String jsonBody = jsonObject.toString();

            // Request
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

            System.out.println("Verihubs\t: WA Verify ("+this.otp+"): " + jsonResponse);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
