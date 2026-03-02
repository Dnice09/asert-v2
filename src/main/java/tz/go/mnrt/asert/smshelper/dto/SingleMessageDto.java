
package tz.go.mnrt.asert.smshelper.dto;

/**
 * A Data Transfer Object (DTO) for SMS messages.
 */
public class SingleMessageDto {
    private String from;
    private String text;
    private String to;

    /**
     * Default no-args constructor.
     */
    public SingleMessageDto() {
    }

    /**
     * All-args constructor.
     *
     * @param from The sender of the SMS.
     * @param text The message content of the SMS.
     * @param to   The recipient of the SMS.
     */
    public SingleMessageDto(String from, String text, String to) {
        this.from = from;
        this.text = text;
        this.to = to;
    }

    /**
     * Gets the sender of the SMS.
     *
     * @return The sender.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the sender of the SMS.
     *
     * @param from The sender.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets the message content of the SMS.
     *
     * @return The message content.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the message content of the SMS.
     *
     * @param message The message content.
     */
    public void setText(String message) {
        this.text = message;
    }

    /**
     * Gets the recipient of the SMS.
     *
     * @return The recipient.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the recipient of the SMS.
     *
     * @param to The recipient.
     */
    public void setTo(String to) {
        this.to = to;
    }
}
