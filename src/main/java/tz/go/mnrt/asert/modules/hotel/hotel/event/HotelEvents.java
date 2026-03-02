package tz.go.mnrt.asert.modules.hotel.hotel.event;

/**
 * Events that can trigger state transitions in the hotel registration process.
 */
public enum HotelEvents {
    // Initial submission
    SUBMIT_HOTEL,

    // Admin review outcomes
    APPROVE_SUBMISSION,
    REJECT_SUBMISSION,

    // Payment events
    GENERATE_PAYMENT,
    PAYMENT_RECEIVED,
    PAYMENT_FAILED,

    // Assessment events
    START_ASSESSMENT,
    COMPLETE_ASSESSMENT,

    // Grading review events
    APPROVE_GRADING,
    REJECT_GRADING,

    // License events
    ISSUE_LICENSE,
    EXPIRE_LICENSE,
    RENEW_LICENSE,
    REVOKE_LICENSE,

    // Resubmission after rejection
    RESUBMIT_HOTEL
}
