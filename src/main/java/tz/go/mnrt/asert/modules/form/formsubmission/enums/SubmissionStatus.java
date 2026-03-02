package tz.go.mnrt.asert.modules.form.formsubmission.enums;

/**
 * Represents the workflow status of a form submission.
 *
 * Workflow:
 * DRAFT → SUBMITTED → APPROVED/REJECTED
 *
 * - DRAFT: Assessor is working on the submission, can make edits and variance corrections
 * - SUBMITTED: Assessor has submitted for DT (Director of Tourism) approval
 * - APPROVED: DT has approved the submission (appears in hotel assessment results)
 * - REJECTED: DT has rejected the submission (goes back to DRAFT for assessor to revise)
 */
public enum SubmissionStatus {
    /**
     * Initial state - assessor working on assessment
     * Multiple DRAFT submissions allowed per assessor+hotel+form
     */
    DRAFT,

    /**
     * Assessor submitted for DT review
     * Awaiting approval or rejection
     */
    SUBMITTED,

    /**
     * DT approved - submission is now "live"
     * Only APPROVED submissions appear in hotel assessment results
     * Only ONE APPROVED submission allowed per assessor+hotel+form
     */
    APPROVED,

    /**
     * DT rejected - returned to DRAFT status
     * Assessor can revise and resubmit
     */
    REJECTED
}
