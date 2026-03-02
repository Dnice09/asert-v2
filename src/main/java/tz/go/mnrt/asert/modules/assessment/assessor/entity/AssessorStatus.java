package tz.go.mnrt.asert.modules.assessment.assessor.entity;

public enum AssessorStatus {
    DRAFT,      // Initial state - can be edited
    PENDING,    // Submitted for review - cannot be edited
    APPROVED,   // Approved by admin - cannot be edited
    REJECTED    // Rejected by admin - can be edited and resubmitted
}
