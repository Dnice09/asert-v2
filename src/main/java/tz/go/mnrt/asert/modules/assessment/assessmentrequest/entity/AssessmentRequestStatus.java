package tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity;

public enum AssessmentRequestStatus {
    PENDING("Pending Review"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SCHEDULED("Assessment Scheduled"),
    IN_PROGRESS("Assessment In Progress"),
    COMPLETED("Assessment Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    AssessmentRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}