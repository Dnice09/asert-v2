package tz.go.mnrt.asert.enums;

import java.util.HashMap;
import java.util.Map;

public class ControlNumberBillStatus {
    private static final Map<String, String> responseMessages = new HashMap<>();

    static {
        responseMessages.put("GF", "ZANMALIPO_FAILURE");
        responseMessages.put("GS", "ZANMALIPO_SUCCESS");
    }

    /**
     * Returns a message corresponding to the given code.
     *
     * @param code the code for which to retrieve the message
     * @return the message corresponding to the given code
     * @throws IllegalArgumentException if the code is not recognized
     */
    public static String getResponseMessage(String code) {
        String message = responseMessages.get(code);
        if (message == null) {
            throw new IllegalArgumentException("Unknown code: " + code);
        }
        return message;
    }
}
