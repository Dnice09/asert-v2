package tz.go.mnrt.asert.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing various helper methods for file conversion, hashing,
 * date-time formatting, and string manipulation.
 */
public final class Util {

    /**
     * Converts a byte array to a Base64 encoded string.
     *
     * @param file the byte array to be converted
     * @return the Base64 encoded string representation of the byte array
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * byte[] fileBytes = Files.readAllBytes(Paths.get("path/to/file"));
     * String base64String = Util.convertFileToBase64(fileBytes);
     * System.out.println(base64String);
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "U29tZSBiYXNlNjQgZW5jb2RlZCBzdHJpbmc="
     * }</pre>
     */
    public static String convertFileToBase64(byte[] file) {
        return Base64.getEncoder().encodeToString(file);
    }

    /**
     * Converts the contents of a file to a Base64 encoded string.
     *
     * @param file the file to be converted
     * @return the Base64 encoded string representation of the file contents
     * @throws IOException if an I/O error occurs reading from the file
     *
     *                     <p>
     *                     Example usage:
     *                     </p>
     *
     *                     <pre>{@code
     *                                         File file = new File("path/to/file");
     *                                         String base64String = Util.convertFileToBase64(file);
     *                                         System.out.println(base64String);
     *                                         }</pre>
     *
     *                     <p>
     *                     Example return value:
     *                     </p>
     *
     *                     <pre>{@code
     *                                         "U29tZSBiYXNlNjQgZW5jb2RlZCBzdHJpbmc="
     *                                         }</pre>
     */
    public static String convertFileToBase64(File file) throws IOException {
        return Base64.getEncoder().encodeToString(convertToByteArray(file));
    }

    /**
     * Converts the contents of a file to a byte array.
     *
     * @param file the file to be converted
     * @return the byte array representation of the file contents
     * @throws IOException if an I/O error occurs reading from the file
     *
     *                     <p>
     *                     Example usage:
     *                     </p>
     *
     *                     <pre>{@code
     *                                         File file = new File("path/to/file");
     *                                         byte[] fileBytes = Util.convertToByteArray(file);
     *                                         System.out.println(Arrays.toString(fileBytes));
     *                                         }</pre>
     *
     *                     <p>
     *                     Example return value:
     *                     </p>
     *
     *                     <pre>{@code
     *                                         [115, 111, 109, 101, 32, 98, 121, 116, 101, 32, 100, 97, 116, 97]
     *                                         }</pre>
     */
    public static byte[] convertToByteArray(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }

    /**
     * Hashes a string using the MD5 algorithm.
     *
     * @param string the string to be hashed
     * @return the MD5 hash of the string
     * @throws Exception if the MD5 algorithm is not available
     *
     *                   <p>
     *                   Example usage:
     *                   </p>
     *
     *                   <pre>{@code
     *                                     String text = "example";
     *                                     String hash = Util.harshMethod(text);
     *                                     System.out.println(hash);
     *                                     }</pre>
     *
     *                   <p>
     *                   Example return value:
     *                   </p>
     *
     *                   <pre>{@code
     *                                     "1a79a4d60de6718e8e5b326e338ae533"
     *                                     }</pre>
     */
    public static String harshMethod(String string) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(string.getBytes());

        byte[] byteData = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte byteDatum : byteData) {
            sb.append(Integer.toString((byteDatum & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }

    /**
     * Parses a resource string and returns the base name without the trailing
     * identifier.
     *
     * @param resource the resource string to be parsed
     * @return the base name of the resource
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * String resource = "ResourceR123";
     * String baseName = Util.parseResource(resource);
     * System.out.println(baseName); // Output: Resource
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "Resource"
     * }</pre>
     */
    public static String parseResource(String resource) {
        String[] names = resource.split("R");
        String lastWord = names[names.length - 1];

        return resource.substring(0, resource.length() - lastWord.length() - 1);
    }

    /**
     * Generates a LocalDateTime object representing a future date and time based on
     * the specified number of days from now.
     *
     * @param days the number of days from now to calculate the future date and time
     * @return the LocalDateTime object representing the future date and time
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * LocalDateTime futureDateTime = Util.getFutureDateTimeInDays(365);
     * System.out.println(Util.formatDateTime(futureDateTime));
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "2025-07-04T00:00:00"
     * }</pre>
     */
    public static String getFutureDateTimeInDays(int days) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Add the specified number of days
        return formatDateTime(now.plusDays(days).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    /**
     * Generates a LocalDateTime object representing a past date and time based on
     * the specified number of days from now.
     *
     * @param days the number of days from now to calculate the future date and time
     * @return the LocalDateTime object representing the future date and time
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * LocalDateTime futureDateTime = Util.getFutureDateTimeInDays(365);
     * System.out.println(Util.formatDateTime(futureDateTime));
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "2025-07-04T00:00:00"
     * }</pre>
     */
    public static String getPastDateTimeInDays(int days) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Add the specified number of days
        return formatDateTime(now.minusDays(days).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    /**
     * Generates a LocalDateTime object representing the current date and time.
     *
     * @return the LocalDateTime object representing the current date and time
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * LocalDateTime currentDateTime = Util.getCurrentDateTime();
     * System.out.println(Util.formatDateTime(currentDateTime));
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "2024-07-04T00:00:00"
     * }</pre>
     */
    public static LocalDateTime getCurrentDateTime() {
        // Get the current date and time
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Formats a LocalDateTime object into a string with the format
     * "yyyy-MM-dd'T'HH:mm:ss".
     *
     * @param dateTime the LocalDateTime object to be formatted
     * @return the formatted date-time string
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>{@code
     * LocalDateTime dateTime = LocalDateTime.now();
     * String formattedDateTime = Util.formatDateTime(dateTime);
     * System.out.println(formattedDateTime);
     * }</pre>
     *
     * <p>
     * Example return value:
     * </p>
     *
     * <pre>{@code
     * "2024-07-04T00:00:00"
     * }</pre>
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Format the LocalDateTime object
        return dateTime.format(formatter);
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTimeString, String... pattern) {
        String datePattern = pattern.length > 0 ? pattern[0] : "yyyy-MM-dd'T'HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        try {
            // Parse the date-time string into a LocalDateTime
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date-time format: " + dateTimeString);
            // Handle exception as needed, for now returning null
            return null;
        }
    }

    public static String readableLocalDate(LocalDate date) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return (date != null ? date : LocalDate.now()).format(formatters);
    }

    public static boolean isValidTzPhoneNumber(String phoneNumber) {
        String tanzaniaPhoneRegex = "(\\+255|255|0)(61|62|63|64|65|66|67|68|69|71|72|73|74|75|76|77|78|79)\\d{7}";
        Pattern pattern = Pattern.compile(tanzaniaPhoneRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
