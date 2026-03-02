package tz.go.mnrt.asert.modules.form.formsubmission.rest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionRequestDto;
import tz.go.mnrt.asert.modules.form.formsubmission.services.FormSubmissionService;

@RestController
@RequestMapping(Constant.API_V1 + "/form-submissions")
@RequiredArgsConstructor
@Slf4j
public class FormSubmissionResource {

    final FormSubmissionService formSubmissionService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(
                formSubmissionService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody FormSubmissionRequestDto formRequestDto) {
        if (formRequestDto.getId() != null || formRequestDto.getUuid() != null) {
            throw new ValidationException("New Form cannot contain id or uuid");
        }

        // Get the variance resolution flag from the request body
        boolean isVarianceResolution = Boolean.TRUE.equals(formRequestDto.getIsVarianceResolution());

        System.out.println("isVarianceResolution: " + isVarianceResolution);

        // IMPORTANT: Check for existing DRAFT submission first (unless this is variance resolution)
        // If a DRAFT exists, update it instead of creating a duplicate
        if (formRequestDto.getHotelUuid() != null && formRequestDto.getFormUuid() != null && !isVarianceResolution) {
            var existingDraft = formSubmissionService.findExistingDraft(
                    formRequestDto.getFormUuid(),
                    formRequestDto.getHotelUuid());

            if (existingDraft.isPresent()) {
                // Update existing DRAFT instead of creating new
                var draft = existingDraft.get();
                formRequestDto.setId(draft.getId());
                formRequestDto.setUuid(draft.getUuid());

                log.info("Updating existing DRAFT submission {} instead of creating duplicate",
                        draft.getUuid());

                return CustomApiResponse.accepted("Draft updated successfully",
                        formSubmissionService.save(formRequestDto));
            }
        }

        // Check if the current assessor has already submitted an APPROVED assessment for this hotel
        // Only APPROVED submissions count as duplicates - DRAFT, SUBMITTED, and REJECTED do not
        // Also allow re-submission if this is a variance resolution
        if (formRequestDto.getHotelUuid() != null && formRequestDto.getFormUuid() != null && !isVarianceResolution) {
            boolean hasApproved = formSubmissionService.hasApprovedSubmission(
                    formRequestDto.getFormUuid(),
                    formRequestDto.getHotelUuid());

            if (hasApproved) {
                throw new ValidationException(
                        "You already have an approved assessment for this facility. " +
                        "To submit a new assessment, your previous approved submission must be withdrawn or rejected.");
            }
        }

        // Validate minimum score requirement
        // validateMinimumScore(formRequestDto);

        return CustomApiResponse.created("Successfully Assessment", formSubmissionService.save(formRequestDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody FormSubmissionRequestDto formDto, @PathVariable UUID uuid) {
        if (formDto.getUuid() == null || !Objects.equals(formDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Form uuid must be present and equals to path uuid {" + uuid + "}");
        }

        return CustomApiResponse.accepted("Form updated successfully", formSubmissionService.save(formDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(formSubmissionService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        formSubmissionService.delete(uuid);
        return CustomApiResponse.noContent("Form deleted successfully");
    }

    @GetMapping("/form/{formUuid}")
    public CustomApiResponse findByFormUuid(
            @PathVariable("formUuid") UUID formUuid,
            Pageable pagination,
            @RequestParam(required = false) Map<String, String> search) {

        return CustomApiResponse.ok(
                formSubmissionService.findByFormUuid(
                        formUuid,
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @GetMapping("/{uuid}/score")
    public CustomApiResponse getSubmissionScore(@PathVariable UUID uuid) {
        return CustomApiResponse.ok(formSubmissionService.getScoreSummary(uuid));
    }

    @GetMapping("/{submissionUuid}/sections/{sectionUuid}/score")
    public CustomApiResponse getSectionScore(
            @PathVariable UUID submissionUuid,
            @PathVariable UUID sectionUuid) {
        return CustomApiResponse.ok(formSubmissionService.getSectionScore(submissionUuid, sectionUuid));
    }

    @PutMapping("/{uuid}/recalculate-scores")
    @Transactional
    public CustomApiResponse recalculateScores(@PathVariable UUID uuid) {
        return CustomApiResponse.accepted("Scores recalculated successfully",
                formSubmissionService.recalculateScores(uuid));
    }

    /**
     * Submit a DRAFT submission for DT approval.
     * Changes status from DRAFT to SUBMITTED.
     */
    @PutMapping("/{uuid}/submit-for-approval")
    @Transactional
    public CustomApiResponse submitForApproval(@PathVariable UUID uuid) {
        return CustomApiResponse.accepted("Submission submitted for approval successfully",
                formSubmissionService.submitForApproval(uuid));
    }

    /**
     * Approve a SUBMITTED submission (DT only).
     * Changes status from SUBMITTED to APPROVED.
     */
    @PutMapping("/{uuid}/approve")
    @Transactional
    public CustomApiResponse approveSubmission(@PathVariable UUID uuid) {
        return CustomApiResponse.accepted("Submission approved successfully",
                formSubmissionService.approveSubmission(uuid));
    }

    /**
     * Reject a SUBMITTED submission (DT only).
     * Changes status from SUBMITTED to REJECTED.
     */
    @PutMapping("/{uuid}/reject")
    @Transactional
    public CustomApiResponse rejectSubmission(
            @PathVariable UUID uuid,
            @RequestParam String rejectionReason) {
        return CustomApiResponse.accepted("Submission rejected successfully",
                formSubmissionService.rejectSubmission(uuid, rejectionReason));
    }

    /**
     * Get all submissions for the currently authenticated assessor.
     * Returns paginated list of submissions ordered by most recent first.
     */
    @GetMapping("/my-submissions")
    public CustomApiResponse getMySubmissions(Pageable pagination) {
        return CustomApiResponse.ok(
                formSubmissionService.getMySubmissions(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("submittedAt").descending()))));
    }

    /**
     * Get the latest submission for the currently authenticated assessor for a specific form and hotel.
     * Used for variance resolution to load the assessor's most recent submission with all responses.
     * Returns the most recent submission ordered by submittedAt DESC.
     *
     * @param formUuid The form UUID
     * @param hotelUuid The hotel UUID
     * @return CustomApiResponse containing the latest submission with responses and scores, or 404 if none exists
     */
    @GetMapping("/latest-for-variance-resolution")
    public CustomApiResponse getLatestSubmissionForVarianceResolution(
            @RequestParam UUID formUuid,
            @RequestParam UUID hotelUuid) {

        log.info("REST: Getting latest submission for variance resolution - form={}, hotel={}", formUuid, hotelUuid);

        var submission = formSubmissionService.findLatestSubmissionForCurrentAssessor(formUuid, hotelUuid);

        if (submission == null) {
            return CustomApiResponse.notFound("No submission found for the current assessor on this form and hotel");
        }

        return CustomApiResponse.ok(submission);
    }

    /**
     * Validates that the form submission meets the minimum score requirement of
     * 75%.
     * This method can be easily commented out to disable score validation if
     * needed.
     *
     * @param formRequestDto the form submission request
     * @throws ValidationException if the score is below the minimum threshold
     */
    private void validateMinimumScore(FormSubmissionRequestDto formRequestDto) {
        // Check if percentage is provided in the request
        if (formRequestDto.getPercentage() != null) {
            double percentage = formRequestDto.getPercentage();
            final double MINIMUM_SCORE_PERCENTAGE = 75.0;

            if (percentage < MINIMUM_SCORE_PERCENTAGE) {
                throw new ValidationException(
                        String.format("Assessment score of %.1f%% does not meet the minimum requirement of %.1f%%. " +
                                "Please review and improve the assessment before submitting.",
                                percentage, MINIMUM_SCORE_PERCENTAGE));
            }
        }
        // If percentage is not provided, the validation will be skipped
        // The score calculation happens in the service layer after this validation
    }
}
