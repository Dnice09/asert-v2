package tz.go.mnrt.asert.smshelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;
import tz.go.mnrt.asert.smshelper.dto.MultiMessageDto;
import tz.go.mnrt.asert.smshelper.dto.SingleMessageDto;

/**
 * Utility class responsible for sending SMS messages.
 * Utilizes Apache HTTP client to send SMS messages via HTTP requests.
 */
public class SmsUtil {

    private static final Logger logger = LoggerFactory.getLogger(SmsUtil.class);
    private static final Dotenv dotenv = Dotenv.load();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends an SMS message containing a control number to the specified phone
     * numbers.
     * This method is synchronous and blocks until the message is sent or an error
     * occurs.
     *
     * @param phoneNumbers the phone numbers to send the SMS to
     * @param message      the message content to be sent
     */
    public void sendMessage(ArrayList<String> phoneNumbers, String message) {
        String endpointUrl = dotenv.get("SMS_ENDPOINT");
        String password = dotenv.get("SMS_PASSWORD");
        String senderId = dotenv.get("SMS_SENDER_ID");
        boolean isMultiple = phoneNumbers.size() > 1;
        String endPoint = String.format("%s/%s", endpointUrl, isMultiple ? "multi" : "single");

        List<String> formattedNumbers = new ArrayList<>();
        for (String number : phoneNumbers) {
            formattedNumbers.add(formatPhoneNumber(number));
        }

        logger.info("Sending SMS to phoneNumbers: {}", formattedNumbers);

        String authInfo = String.format("Basic %s", password);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = createHttpPost(endPoint, authInfo);

            if (isMultiple) {
                MultiMessageDto smsMessage = new MultiMessageDto(senderId, message, formattedNumbers);
                sendRequest(httpClient, httpPost, smsMessage);
            } else {
                SingleMessageDto smsMessage = new SingleMessageDto(senderId, message, formattedNumbers.get(0));
                sendRequest(httpClient, httpPost, smsMessage);
            }
        } catch (IOException e) {
            logger.error("Failed to send SMS", e);
        }
    }

    private HttpPost createHttpPost(String url, String authInfo) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", authInfo);
        return httpPost;
    }

    private void sendRequest(CloseableHttpClient httpClient, HttpPost httpPost, Object smsMessage) throws IOException {
        String jsonData = objectMapper.writeValueAsString(smsMessage);
        StringEntity data = new StringEntity(jsonData, StandardCharsets.UTF_8);
        httpPost.setEntity(data);

        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("Response Code: {}", statusCode);

        String responseBody = EntityUtils.toString(response.getEntity());
        logger.debug("Response Data: {}", responseBody);
    }

    /**
     * Formats a phone number by adding the country code if it starts with '0'.
     * Assumes the country code is +255.
     *
     * @param phoneNumber the phone number to format
     * @return the formatted phone number with the country code
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+255" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
