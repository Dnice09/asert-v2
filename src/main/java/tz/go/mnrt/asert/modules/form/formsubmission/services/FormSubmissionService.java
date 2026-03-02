package tz.go.mnrt.asert.modules.form.formsubmission.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionRequestDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionResponseDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionScoreSummaryDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionSummaryDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelAssessmentResultDto;

public interface FormSubmissionService {

    FormSubmissionRequestDto save(FormSubmissionRequestDto formDto);

    Page<FormSubmissionResponseDto> findAll(Pageable page, Map<String, String> search);

    FormSubmissionResponseDto findByUuid(UUID id);

    void delete(UUID uuid);

    Page<FormSubmissionResponseDto> findByFormUuid(UUID formUuid, Pageable page, Map<String, String> search);

    FormSubmissionScoreSummaryDto getScoreSummary(UUID formSubmissionUuid);

    SectionScoreDto getSectionScore(UUID formSubmissionUuid, UUID sectionUuid);

    FormSubmissionResponseDto recalculateScores(UUID uuid);

    HotelAssessmentResultDto getHotelAssessmentResult(UUID hotelUuid);

    Page<FormSubmissionSummaryDto> getHotelSubmissions(UUID hotelUuid, Pageable pageable, String assessor);

    boolean existsByFormUuidAndHotelUuidAndSubmittedBy(UUID formUuid, UUID hotelUuid, String submittedBy);

    boolean existsByFormUuidAndHotelUuidAndAssessorId(UUID formUuid, UUID hotelUuid, Long assessorId);

    boolean checkDuplicateSubmission(UUID formUuid, UUID hotelUuid);

    /**
     * Check if the current user has an APPROVED submission for the given form and hotel.
     * DRAFT, SUBMITTED, and REJECTED submissions do not count as duplicates.
     * Only APPROVED submissions block new submissions.
     */
    boolean hasApprovedSubmission(UUID formUuid, UUID hotelUuid);

    /**
     * Submit a DRAFT submission for DT approval.
     * Changes status from DRAFT to SUBMITTED.
     * Sets submittedForApprovalAt timestamp.
     */
    FormSubmissionResponseDto submitForApproval(UUID submissionUuid);

    /**
     * Approve a SUBMITTED submission (DT only).
     * Changes status from SUBMITTED to APPROVED.
     * Sets approvedAt timestamp.
     */
    FormSubmissionResponseDto approveSubmission(UUID submissionUuid);

    /**
     * Reject a SUBMITTED submission (DT only).
     * Changes status from SUBMITTED to REJECTED.
     * Sets rejectedAt timestamp and rejection reason.
     */
    FormSubmissionResponseDto rejectSubmission(UUID submissionUuid, String rejectionReason);

    /**
     * Get all submissions for the currently authenticated assessor.
     * Finds the assessor associated with the current user and returns all their submissions.
     * Used for "My Assessments" page.
     */
    Page<FormSubmissionResponseDto> getMySubmissions(Pageable pageable);

    /**
     * Find an existing DRAFT submission for the current user for a specific form and hotel.
     * Used to prevent duplicate DRAFT submissions - if a DRAFT exists, it should be updated instead of creating new.
     * Returns the most recent DRAFT if multiple exist.
     *
     * @param formUuid The form UUID
     * @param hotelUuid The hotel UUID
     * @return Optional containing the existing DRAFT submission, or empty if none exists
     */
    java.util.Optional<tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission> findExistingDraft(UUID formUuid, UUID hotelUuid);

    /**
     * Find the latest submission for the current assessor for a specific form and hotel.
     * Used for variance resolution to load the assessor's most recent submission with all responses.
     * Returns the submission in draft-compatible format so frontend can load it the same way as drafts.
     *
     * @param formUuid The form UUID
     * @param hotelUuid The hotel UUID
     * @return The latest submission in draft format, or null if none exists
     */
    tz.go.mnrt.asert.modules.form.formsubmission.dtos.SubmissionAsDraftDto findLatestSubmissionForCurrentAssessor(UUID formUuid, UUID hotelUuid);
}
