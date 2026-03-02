package tz.go.mnrt.asert.smshelper.dto;

import java.util.List;

/**
 * A Data Transfer Object (DTO) for SMS messages.
 */
public class MultiMessageDto {
    private String from;
    private String text;
    private List<String> to;

    /**
     * Default no-args constructor.
     */
    public MultiMessageDto() {
    }

    /**
     * All-args constructor.
     *
     * @param from The sender of the SMS.
     * @param text The message content of the SMS.
     * @param to   The recipients of the SMS.
     */
    public MultiMessageDto(String from, String text, List<String> to) {
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
     * @param text The message content.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the recipients of the SMS.
     *
     * @return The recipients.
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * Sets the recipients of the SMS.
     *
     * @param to The recipients.
     */
    public void setTo(List<String> to) {
        this.to = to;
    }
}
