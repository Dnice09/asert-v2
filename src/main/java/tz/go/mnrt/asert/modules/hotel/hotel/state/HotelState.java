package tz.go.mnrt.asert.modules.hotel.hotel.state;

public enum HotelState {
    // Initial state when hotel is first created
    DRAFT,

    SUBMITED_FOR_GRADING,

    // Hotel has been submitted for review
    AWAITING_APPROVAL,

    // Hotel was rejected during initial review
    REJECTED,

    // Hotel was approved and now waiting for grading fee payment
    AWAITING_GRADING_FEE,

    // Payment attempt failed
    PAYMENT_FAILED,

    // Hotel is being assessed by assessors
    UNDER_ASSESSMENT,

    // Assessment is complete and waiting for admin review
    AWAITING_GRADING_REVIEW,

    // Hotel has been graded and license issued
    GRADED,

    // License has expired
    LICENSE_EXPIRED,

    // License has been revoked
    LICENSE_REVOKED,

    AWAITING_INSPECTION,

    AWAITING_APPLICATION_FEES,

    AWAITING_BOARD_REVIEW,

    AWAITING_HFR_CODES,

    AWAITING_REGISTRATION_FEES,

    INCOMPLETE_SUBMISSION,

    FAILED_INSPECTION,

    APPLICATION_EXPIRED,

    REGISTRATION_EXPIRED,

    FAILED_BOARD_REVIEW,

    FACILITY_REGISTERED
}
