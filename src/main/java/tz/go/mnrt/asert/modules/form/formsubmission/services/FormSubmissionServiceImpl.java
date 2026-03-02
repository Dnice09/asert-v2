package tz.go.mnrt.asert.modules.form.formsubmission.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AggregatedSectionScoreDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorSubmissionDto;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldResponseValueDto;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formfield.repository.FormFieldRepository;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;
import tz.go.mnrt.asert.modules.form.formfieldresponse.entity.FormFieldResponse;
import tz.go.mnrt.asert.modules.form.formfieldresponse.repository.FormFieldResponseRepository;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.form.formsection.repository.FormSectionRepository;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.CategoryScoreDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionRequestDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionResponseDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionScoreSummaryDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionSummaryDto;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.SubmissionAsDraftDto;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmission.enums.SubmissionStatus;
import tz.go.mnrt.asert.modules.form.formsubmission.repository.FormSubmissionRepository;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.entity.FormSubmissionScore;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.repository.FormSubmissionScoreRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelAssessmentResultDto;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.ratingcriteria.services.RatingCriteriaService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.services.AssessmentVarianceLogService;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormSubmissionServiceImpl extends SimpleSearchService<FormSubmission> implements FormSubmissionService {
    private final FormSubmissionRepository formSubmissionRepository;
    private final FormFieldResponseRepository fieldResponseRepository;
    private final FormRepository formRepository;
    private final FormFieldRepository formFieldRepository;
    private final HotelRepository hotelRepository;
    private final FormSubmissionScoreRepository submissionScoreRepository;
    private final FormSectionRepository formSectionRepository;
    private final RatingCriteriaService ratingCriteriaService;
    private final AssessorRepository assessorRepository;
    private final UserService userService;
    private final AssessmentVarianceLogService assessmentVarianceLogService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public FormSubmissionRequestDto save(FormSubmissionRequestDto formSubmissionRequestDto) {
        log.info("Saving form submission: {}", formSubmissionRequestDto);

        // Get the form
        Form form = formRepository.findByUuid(formSubmissionRequestDto.getFormUuid())
                .orElseThrow(() -> new ValidationException(
                        "Form with uuid {" + formSubmissionRequestDto.getFormUuid() + "} not found"));

        // Validate section hierarchy to ensure max scores are consistent
        validateSectionHierarchy(form.getSections());

        // Get the hotel
        Hotel hotel = null;
        if (formSubmissionRequestDto.getHotelUuid() != null) {
            hotel = hotelRepository.findByUuid(formSubmissionRequestDto.getHotelUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Hotel with uuid {" + formSubmissionRequestDto.getHotelUuid() + "} not found"));

            // Validate that the hotel type matches one of the form's property types
            if (!form.getPropertyTypes().contains(hotel.getPropertyType())) {
                throw new ValidationException("This form cannot be submitted for this type of hotel. " +
                        "Form supports types: " + form.getPropertyTypes() + ", Hotel type: " + hotel.getPropertyType());
            }
        } else {
            // If hotel is required but not provided
            if (form.getPropertyTypes() != null && !form.getPropertyTypes().isEmpty()) {
                throw new ValidationException(
                        "This form requires a hotel of one of these types: " + form.getPropertyTypes());
            }
        }

        // Create or retrieve the submission
        FormSubmission formSubmission;
        if (formSubmissionRequestDto.getUuid() != null) {
            formSubmission = formSubmissionRepository
                    .findByUuid(formSubmissionRequestDto.getUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Form submission with uuid {" + formSubmissionRequestDto.getUuid() + "} not found"));

            // For updates, remove existing responses and scores to replace with new ones
            if (formSubmission.getResponses() != null) {
                fieldResponseRepository.deleteAll(formSubmission.getResponses());
                formSubmission.getResponses().clear();
            }

            // Delete existing scores
            submissionScoreRepository.deleteBySubmissionId(formSubmission.getId());

            // Update the hotel if it's changed
            formSubmission.setHotel(hotel);
        } else {
            // For new submissions, get the current assessor
            LoggedInUserDto loggedInUser = userService.loggedIn()
                    .orElseThrow(() -> new ValidationException("User not authenticated"));

            Assessor assessor = assessorRepository.findByUserEmail(loggedInUser.getEmail())
                    .orElseThrow(() -> new ValidationException("Current user is not an assessor"));

            formSubmission = FormSubmission.builder()
                    .submittedBy(formSubmissionRequestDto.getSubmittedBy())
                    .hotel(hotel)
                    .submittedAt(LocalDateTime.now())
                    .form(form)
                    .hotel(hotel)
                    .build();
            formSubmission.setUuid(UUID.randomUUID());
            formSubmission.setAssessor(assessor);
        }

        formSubmission.setHotel(hotel);

        // Save the submission first to get the ID
        formSubmission = formSubmissionRepository.save(formSubmission);

        // Process each field response
        if (formSubmissionRequestDto.getResponses() != null && !formSubmissionRequestDto.getResponses().isEmpty()) {
            List<FormFieldResponse> responses = new ArrayList<>();

            for (FormFieldResponseValueDto responseDto : formSubmissionRequestDto.getResponses()) {
                // Find the field
                FormField field = formFieldRepository.findByUuid(responseDto.getFieldUuid())
                        .orElseThrow(() -> new ValidationException(
                                "Field with UUID " + responseDto.getFieldUuid() + " not found"));

                // Validate field option scores
                validateFieldOptionScores(field);

                // Create the response entity
                FormFieldResponse response = FormFieldResponse.builder()
                        .submission(formSubmission)
                        .field(field)
                        .value(responseDto.getValue())
                        .comments(responseDto.getComments() != null ? responseDto.getComments() : "N/A")
                        .build();
                response.setUuid(UUID.randomUUID());

                // Save the response
                response = fieldResponseRepository.save(response);
                responses.add(response);
            }

            // Set responses to the submission
            formSubmission.setResponses(responses);
        }

        // Calculate and save scores
        calculateAndSaveScores(formSubmission);

        // Flush to ensure scores are visible to subsequent queries
        entityManager.flush();

        // Detect variances after submission (for hotel assessments only)
        if (hotel != null) {
            try {
                // Check if this is a variance resolution submission
                boolean isVarianceResolution = Boolean.TRUE.equals(formSubmissionRequestDto.getIsVarianceResolution());

                // ALWAYS check for existing variances first, regardless of isVarianceResolution
                // flag
                // This ensures that any new submission can automatically resolve variances
                var existingVariances = assessmentVarianceLogService.getUnresolvedVariances(
                        hotel.getId(), form.getId());

                boolean hasExistingVariances = existingVariances != null && !existingVariances.isEmpty();

                if (hasExistingVariances) {
                    log.info(
                            "Found {} existing variances, checking if submission resolves them: hotel={}, form={}, assessor={}",
                            existingVariances.size(), hotel.getId(), form.getId(),
                            formSubmission.getAssessor().getId());

                    // Check and resolve existing variances that may now be within threshold
                    // This updates variance scores and status (OPEN → PARTIALLY_RESOLVED →
                    // RESOLVED)
                    assessmentVarianceLogService.checkAndResolveVariances(formSubmission);

                    // Re-check for remaining unresolved variances after resolution attempt
                    var remainingVariances = assessmentVarianceLogService.getUnresolvedVariances(
                            hotel.getId(), form.getId());

                    if (remainingVariances != null && !remainingVariances.isEmpty()) {
                        formSubmission.setVarianceCheckStatus("HAS_VARIANCE");
                        formSubmission.setHasUnresolvedVariances(true);
                        log.info("Variance check complete - {} variances still exist for submission {}",
                                remainingVariances.size(), formSubmission.getUuid());
                    } else {
                        formSubmission.setVarianceCheckStatus("NO_VARIANCE");
                        formSubmission.setHasUnresolvedVariances(false);
                        log.info("All variances resolved by submission {}",
                                formSubmission.getUuid());
                    }
                } else {
                    // No existing variances, run detection to check if this submission creates new
                    // ones
                    log.info("Running variance detection for submission: hotel={}, form={}, assessor={}",
                            hotel.getId(), form.getId(), formSubmission.getAssessor().getId());
                    var detectedVariances = assessmentVarianceLogService.detectVariances(formSubmission);

                    // Update variance check status based on detection results
                    if (detectedVariances != null && !detectedVariances.isEmpty()) {
                        formSubmission.setVarianceCheckStatus("HAS_VARIANCE");
                        // Set historical flag - this submission was created when variances existed
                        formSubmission.setHasUnresolvedVariances(true);
                        log.info("Variance detected for submission {}", formSubmission.getUuid());
                    } else {
                        formSubmission.setVarianceCheckStatus("NO_VARIANCE");
                        // Set historical flag - this submission was created with no variances
                        formSubmission.setHasUnresolvedVariances(false);
                        log.info("No variance detected for submission {}", formSubmission.getUuid());
                    }
                }

                // Save the current submission
                formSubmissionRepository.save(formSubmission);

                // Flush to ensure the submission is in the database before batch update
                entityManager.flush();

                // IMPORTANT: Update ALL submissions for this hotel/form with the variance flag
                // This ensures that when a 2nd or 3rd assessor submits, all previous
                // submissions
                // are marked correctly with has_unresolved_variances
                updateAllSubmissionsVarianceFlag(hotel.getId(), form.getId());
            } catch (Exception e) {
                log.error("Error during variance detection for submission {}: {}",
                        formSubmission.getUuid(), e.getMessage(), e);
                // Don't fail the submission if variance detection fails
                // The variance check can be run manually later
            }
        }

        // Set the ID in the response DTO
        formSubmissionRequestDto.setId(formSubmission.getId());
        return formSubmissionRequestDto;
    }

    private void calculateAndSaveScores(FormSubmission submission) {
        Form form = submission.getForm();
        Map<Long, List<FormFieldResponse>> responsesByFieldId = submission.getResponses().stream()
                .collect(Collectors.groupingBy(r -> r.getField().getId()));

        // Get top-level sections
        List<FormSection> topLevelSections = form.getSections().stream()
                .filter(s -> s.getParentSection() == null && !s.isDeleted())
                .collect(Collectors.toList());

        double totalScore = 0;
        double totalMaxPossible = 0;

        // Process each top-level section
        for (FormSection section : topLevelSections) {
            SectionScoreResult sectionScore = calculateSectionScore(section, responsesByFieldId);
            totalScore += sectionScore.getScore();
            totalMaxPossible += sectionScore.getMaxPossible();

            // Save section score
            saveSectionScore(submission, section, sectionScore);
        }

        // Save total score with the submission
        submission.setTotalScore(totalScore);
        submission.setMaxPossibleScore(totalMaxPossible);
        submission.setPercentage(totalMaxPossible > 0 ? (totalScore / totalMaxPossible) * 100 : 0);
        formSubmissionRepository.save(submission);
    }

    private SectionScoreResult calculateSectionScore(FormSection section,
            Map<Long, List<FormFieldResponse>> responsesByFieldId) {
        double sectionScore = 0;
        double sectionMaxPossible = 0;
        List<SectionScoreResult> subsectionScores = new ArrayList<>();

        // Calculate scores for subsections first
        List<FormSection> subsections = section.getSubsections().stream()
                .filter(s -> !s.isDeleted())
                .collect(Collectors.toList());

        for (FormSection subsection : subsections) {
            SectionScoreResult subsectionScore = calculateSectionScore(subsection, responsesByFieldId);
            // Use raw scores without applying weights during calculation
            sectionScore += subsectionScore.getScore();
            sectionMaxPossible += subsectionScore.getMaxPossible();
            subsectionScores.add(subsectionScore);
        }

        // Calculate scores for direct fields in this section
        List<FormField> fields = section.getFields().stream()
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());

        for (FormField field : fields) {
            List<FormFieldResponse> responses = responsesByFieldId.get(field.getId());
            if (responses != null && !responses.isEmpty()) {
                // For checkbox fields - handle multiple separate responses
                if (field.getFieldType().equals("checkbox")) {
                    for (FormFieldResponse response : responses) {
                        String selectedValue = response.getValue();
                        for (FormFieldOption option : field.getOptions()) {
                            if (option.getValue().equals(selectedValue)) {
                                Integer optionScore = option.getScore();
                                if (optionScore != null) {
                                    sectionScore += optionScore;
                                }
                                break;
                            }
                        }
                    }
                } else {
                    // For single-value fields (select, radio, rating), use the first response
                    FormFieldResponse response = responses.get(0);

                    // Find the selected option and its score
                    if (field.getFieldType().equals("select") || field.getFieldType().equals("radio")) {
                        String selectedValue = response.getValue();
                        for (FormFieldOption option : field.getOptions()) {
                            if (option.getValue().equals(selectedValue)) {
                                Integer optionScore = option.getScore();
                                if (optionScore != null) {
                                    sectionScore += optionScore;
                                }
                                break;
                            }
                        }
                    }

                    // For rating fields
                    if (field.getFieldType().equals("rating")) {
                        try {
                            int rating = Integer.parseInt(response.getValue());
                            sectionScore += rating;
                        } catch (NumberFormatException e) {
                            log.error("Error parsing rating value: {}", response.getValue());
                        }
                    }
                }
            }

            // Calculate max possible score for this field - only if section doesn't have a
            // defined max score
            if (section.getMaxScore() == null) {
                if (field.getFieldType().equals("select") || field.getFieldType().equals("radio")) {
                    int maxOptionScore = field.getOptions().stream()
                            .map(o -> o.getScore() != null ? o.getScore() : 0)
                            .max(Integer::compare)
                            .orElse(0);
                    sectionMaxPossible += maxOptionScore;
                } else if (field.getFieldType().equals("rating")) {
                    sectionMaxPossible += 5; // Assuming rating scale is 1-5
                } else if (field.getFieldType().equals("checkbox")) {
                    // For checkbox, max is sum of all options
                    int totalCheckboxScore = field.getOptions().stream()
                            .map(o -> o.getScore() != null ? o.getScore() : 0)
                            .mapToInt(Integer::intValue)
                            .sum();
                    sectionMaxPossible += totalCheckboxScore;
                }
            }
        }

        // If section has a defined max score, use that instead of calculated
        if (section.getMaxScore() != null && section.getMaxScore() > 0) {
            sectionMaxPossible = section.getMaxScore();
        }

        // Don't apply weights during calculation - weights should be used for final
        // form-level aggregation only
        // Store raw scores in the database
        return new SectionScoreResult(section, sectionScore, sectionMaxPossible, subsectionScores);
    }

    private void saveSectionScore(FormSubmission submission, FormSection section, SectionScoreResult scoreResult) {
        FormSubmissionScore score = new FormSubmissionScore();
        score.setUuid(UUID.randomUUID());
        score.setSubmission(submission);
        score.setSection(section);
        score.setScore(scoreResult.getScore());
        score.setMaxPossible(scoreResult.getMaxPossible());
        score.setPercentage(
                scoreResult.getMaxPossible() > 0 ? (scoreResult.getScore() / scoreResult.getMaxPossible()) * 100 : 0);

        submissionScoreRepository.save(score);

        // Save subsection scores
        for (SectionScoreResult subsectionResult : scoreResult.getSubsectionScores()) {
            saveSectionScore(submission, subsectionResult.getSection(), subsectionResult);
        }
    }

    /**
     * Validates that field option scores don't exceed section max score
     */
    private void validateFieldOptionScores(FormField field) {
        if (field.getOptions() != null && !field.getOptions().isEmpty() && field.getSection() != null) {
            FormSection section = field.getSection();

            if (section.getMaxScore() != null) {
                // For select/radio fields, check if any single option exceeds max score
                if (field.getFieldType().equals("select") || field.getFieldType().equals("radio")) {
                    int maxOptionScore = field.getOptions().stream()
                            .filter(o -> o.getScore() != null)
                            .mapToInt(o -> o.getScore())
                            .max()
                            .orElse(0);

                    if (maxOptionScore > section.getMaxScore()) {
                        throw new ValidationException("Field '" + field.getLabel() +
                                "' has option with score (" + maxOptionScore +
                                ") that exceeds section max score (" + section.getMaxScore() +
                                ") for section: " + section.getTitle());
                    }
                }

                // For checkbox fields, check if sum of all options exceeds max score
                if (field.getFieldType().equals("checkbox")) {
                    int totalOptionScore = field.getOptions().stream()
                            .filter(o -> o.getScore() != null)
                            .mapToInt(o -> o.getScore())
                            .sum();

                    if (totalOptionScore > section.getMaxScore()) {
                        throw new ValidationException("Field '" + field.getLabel() +
                                "' has total option scores (" + totalOptionScore +
                                ") that exceed section max score (" + section.getMaxScore() +
                                ") for section: " + section.getTitle());
                    }
                }
            }
        }
    }

    // Add this inner class if it doesn't exist
    public static class SectionScoreResult {
        private final FormSection section;
        private final double score;
        private final double maxPossible;
        private final List<SectionScoreResult> subsectionScores;

        public SectionScoreResult(FormSection section, double score, double maxPossible,
                List<SectionScoreResult> subsectionScores) {
            this.section = section;
            this.score = score;
            this.maxPossible = maxPossible;
            this.subsectionScores = subsectionScores;
        }

        public FormSection getSection() {
            return section;
        }

        public double getScore() {
            return score;
        }

        public double getMaxPossible() {
            return maxPossible;
        }

        public List<SectionScoreResult> getSubsectionScores() {
            return subsectionScores;
        }
    }

    // Remaining methods from your original implementation
    @Override
    public Page<FormSubmissionResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated form submissions with page {} and search {} ", page, search);

        // Extract status parameter and remove it from search map to handle it
        // separately
        Map<String, String> searchCopy = new HashMap<>(search);
        String statusParam = searchCopy.remove("status");

        // Create base specification from remaining search parameters
        var specification = createSpecification(FormSubmission.class, searchCopy);

        // Add status filter if provided
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                SubmissionStatus status = SubmissionStatus.valueOf(statusParam.toUpperCase());
                specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status value: {}", statusParam);
            }
        }

        return formSubmissionRepository
                .findAll(specification, page)
                .map(submission -> {
                    FormSubmissionResponseDto dto = new FormSubmissionResponseDto(submission);
                    dto = dto.withDetails(submission);

                    // Populate assessor count information for multi-assessor validation
                    if (submission.getHotel() != null && submission.getForm() != null) {
                        populateAssessorCounts(dto, submission);
                    }

                    return dto;
                });
    }

    @Override
    public FormSubmissionResponseDto findByUuid(UUID uuid) {
        log.info("Finding form submission with uuid {} ", uuid);
        return formSubmissionRepository
                .findByUuid(uuid)
                .map(submission -> {
                    FormSubmissionResponseDto dto = new FormSubmissionResponseDto(submission);
                    return dto.withDetailsAndScores(submission); // Updated to include scores
                })
                .orElseThrow(() -> new ValidationException("Form submission with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting form submission with uuid {} ", uuid);
        formSubmissionRepository.softDelete(uuid);
    }

    @Override
    public Page<FormSubmissionResponseDto> findByFormUuid(UUID formUuid, Pageable page, Map<String, String> search) {
        log.info("Finding form submissions by form UUID: {}", formUuid);

        // Create a specification that includes the form UUID filter
        Map<String, String> filters = new HashMap<>(search);
        filters.put("form.uuid", formUuid.toString());

        return formSubmissionRepository
                .findAll(createSpecification(FormSubmission.class, filters), page)
                .map(submission -> new FormSubmissionResponseDto(submission).withDetailsAndScores(submission));
    }

    @Override
    public FormSubmissionScoreSummaryDto getScoreSummary(UUID formSubmissionUuid) {
        log.info("Getting score summary for submission with uuid {}", formSubmissionUuid);

        FormSubmission submission = formSubmissionRepository.findByUuid(formSubmissionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with uuid " + formSubmissionUuid + " not found"));

        FormSubmissionScoreSummaryDto summary = new FormSubmissionScoreSummaryDto();
        summary.setSubmissionUuid(submission.getUuid());
        summary.setFormName(submission.getForm().getName());
        summary.setTotalScore(submission.getTotalScore());
        summary.setMaxPossibleScore(submission.getMaxPossibleScore());
        summary.setPercentage(submission.getPercentage());

        // Group scores by top-level sections (categories)
        List<CategoryScoreDto> categoryScores = new ArrayList<>();
        Map<Long, FormSubmissionScore> scoreMap = submission.getSectionScores().stream()
                .collect(Collectors.toMap(score -> score.getSection().getId(), score -> score));

        // Get top-level sections and their scores
        submission.getForm().getSections().stream()
                .filter(section -> !section.isDeleted() && section.getParentSection() == null)
                .forEach(section -> {
                    FormSubmissionScore score = scoreMap.get(section.getId());
                    if (score != null) {
                        CategoryScoreDto categoryScore = new CategoryScoreDto();
                        categoryScore.setCategoryName(section.getTitle());
                        categoryScore.setScore(score.getScore());
                        categoryScore.setMaxPossible(score.getMaxPossible());
                        categoryScore.setPercentage(score.getPercentage());
                        categoryScores.add(categoryScore);
                    }
                });

        summary.setCategoryScores(categoryScores);
        return summary;
    }

    @Override
    public SectionScoreDto getSectionScore(UUID formSubmissionUuid, UUID sectionUuid) {
        log.info("Getting section score for submission {} and section {}",
                formSubmissionUuid, sectionUuid);

        FormSubmission submission = formSubmissionRepository.findByUuid(formSubmissionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with uuid " + formSubmissionUuid + " not found"));

        FormSection section = formSectionRepository.findByUuid(sectionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form section with uuid " + sectionUuid + " not found"));

        // Find the score for this section
        FormSubmissionScore sectionScore = submission.getSectionScores().stream()
                .filter(score -> score.getSection().getId().equals(section.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        "Score not found for section " + sectionUuid + " in submission " + formSubmissionUuid));

        // Create the DTO
        SectionScoreDto scoreDto = new SectionScoreDto();
        scoreDto.setSectionId(section.getId());
        scoreDto.setSectionUuid(section.getUuid());
        scoreDto.setSectionTitle(section.getTitle());
        scoreDto.setScore(sectionScore.getScore());
        scoreDto.setMaxPossible(sectionScore.getMaxPossible());
        scoreDto.setPercentage(sectionScore.getPercentage());

        // Add subsection scores recursively
        if (section.getSubsections() != null && !section.getSubsections().isEmpty()) {
            List<SectionScoreDto> subsectionScores = new ArrayList<>();

            // Only include non-deleted subsections
            section.getSubsections().stream()
                    .filter(subsection -> !subsection.isDeleted())
                    .forEach(subsection -> {
                        try {
                            SectionScoreDto subsectionScore = getSectionScore(formSubmissionUuid, subsection.getUuid());
                            subsectionScores.add(subsectionScore);
                        } catch (ValidationException e) {
                            log.warn("No score found for subsection {}", subsection.getUuid());
                        }
                    });

            scoreDto.setSubsectionScores(subsectionScores);
        }

        return scoreDto;
    }

    @Override
    @Transactional
    public FormSubmissionResponseDto recalculateScores(UUID uuid) {
        log.info("Recalculating scores for submission with uuid {}", uuid);

        FormSubmission submission = formSubmissionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with uuid " + uuid + " not found"));

        // Delete existing scores
        submissionScoreRepository.deleteBySubmissionId(submission.getId());

        // Reset submission score fields
        submission.setTotalScore(null);
        submission.setMaxPossibleScore(null);
        submission.setPercentage(null);

        // Recalculate scores
        calculateAndSaveScores(submission);

        // Return the updated submission with scores
        return new FormSubmissionResponseDto(submission).withDetailsAndScores(submission);
    }

    @Override
    public HotelAssessmentResultDto getHotelAssessmentResult(UUID hotelUuid) {
        log.info("Getting assessment results for hotel with UUID: {}", hotelUuid);

        // Get the hotel
        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        // Create the result DTO
        HotelAssessmentResultDto resultDto = new HotelAssessmentResultDto();
        resultDto.setHotelUuid(hotel.getUuid());
        resultDto.setHotelName(hotel.getName());
        resultDto.setHotelType(hotel.getPropertyType());

        // Find the form for this hotel type
        formRepository.findByPropertyTypesContainingAndIsDeletedFalse(hotel.getPropertyType().name())
                .ifPresent(form -> {
                    resultDto.setFormUuid(form.getUuid());
                    resultDto.setFormName(form.getName());
                });

        // Get all APPROVED submissions for this hotel
        // Only APPROVED submissions count towards the hotel's assessment results
        // DRAFT, SUBMITTED, and REJECTED submissions are not included
        List<FormSubmission> submissions = formSubmissionRepository
                .findByHotelIdAndStatusOrderBySubmittedAtDesc(hotel.getId(), SubmissionStatus.APPROVED);

        if (submissions.isEmpty()) {
            resultDto.setTotalAssessments(0);
            resultDto.setUniqueAssessors(0);
            return resultDto;
        }

        // Set total assessments and last assessed time
        resultDto.setTotalAssessments(submissions.size());
        resultDto.setLastAssessedAt(submissions.get(0).getSubmittedAt());

        // Count unique assessors
        Set<String> uniqueAssessors = submissions.stream()
                .map(fs -> fs.getAssessor().getEmail())
                .collect(Collectors.toSet());
        resultDto.setUniqueAssessors(uniqueAssessors.size());

        // Calculate aggregate score and percentage
        DoubleSummaryStatistics scoreStats = submissions.stream()
                .map(FormSubmission::getTotalScore)
                .filter(Objects::nonNull)
                .collect(Collectors.summarizingDouble(Double::doubleValue));

        DoubleSummaryStatistics percentageStats = submissions.stream()
                .map(FormSubmission::getPercentage)
                .filter(Objects::nonNull)
                .collect(Collectors.summarizingDouble(Double::doubleValue));

        resultDto.setAggregateScore(scoreStats.getAverage());
        resultDto.setAggregatePercentage(percentageStats.getAverage());

        // Set star rating based on aggregate raw score and hotel property type
        // The rating criteria uses raw score ranges (e.g., 1125-1463 for 1 star), not percentages
        if (scoreStats.getCount() > 0) {
            String starRating = ratingCriteriaService.getTopStarRatingByPropertyTypeAndScore(
                    scoreStats.getAverage(),
                    hotel.getPropertyType());
            resultDto.setStarRating(starRating);
            log.info("Hotel {} - Average score: {}, Star rating: {}",
                    hotel.getName(), scoreStats.getAverage(), starRating);
        }

        // Create individual assessor submissions
        List<AssessorSubmissionDto> assessorSubmissions = submissions.stream()
                .map(this::createAssessorSubmissionDto)
                .collect(Collectors.toList());
        resultDto.setAssessorSubmissions(assessorSubmissions);

        // Calculate aggregated section scores
        if (!submissions.isEmpty() && submissions.get(0).getForm() != null) {
            List<AggregatedSectionScoreDto> aggregatedSectionScores = calculateAggregatedSectionScores(submissions,
                    submissions.get(0).getForm());
            resultDto.setSectionScores(aggregatedSectionScores);
        }

        return resultDto;
    }

    private AssessorSubmissionDto createAssessorSubmissionDto(FormSubmission submission) {
        Assessor assessor = submission.getAssessor();
        String assessorFullName = String.format("%s %s",
                assessor.getFirstName() != null ? assessor.getFirstName() : "",
                assessor.getLastName() != null ? assessor.getLastName() : "").trim();

        AssessorSubmissionDto dto = new AssessorSubmissionDto();
        dto.setSubmissionUuid(submission.getUuid());
        dto.setSubmissionId(submission.getId());
        dto.setAssessorName(assessorFullName);
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setTotalScore(submission.getTotalScore());
        dto.setMaxPossibleScore(submission.getMaxPossibleScore());
        dto.setPercentage(submission.getPercentage());
        dto.setStatus(submission.getStatus().name());

        try {
            // Get the form from the submission
            Form form = submission.getForm();
            if (form != null) {
                // Get only top-level sections (sections with no parent)
                List<FormSection> topLevelSections = form.getSections().stream()
                        .filter(section -> section.getParentSection() == null && !section.isDeleted())
                        .collect(Collectors.toList());

                // Create a list to hold the section scores
                List<SectionScoreDto> sectionScores = new ArrayList<>();

                // For each top-level section, get its score (which will include subsections)
                for (FormSection section : topLevelSections) {
                    try {
                        // Use your existing getSectionScore method which already handles subsections
                        SectionScoreDto sectionScore = getSectionScore(submission.getUuid(), section.getUuid());
                        sectionScores.add(sectionScore);
                    } catch (ValidationException e) {
                        log.warn("No score found for section {} in submission {}: {}",
                                section.getUuid(), submission.getUuid(), e.getMessage());
                    }
                }

                // Set the list of section scores in the DTO
                dto.setSectionScores(sectionScores);
            }
        } catch (Exception e) {
            log.warn("Error getting section scores for submission {}: {}",
                    submission.getUuid(), e.getMessage());
        }

        return dto;
    }

    private List<AggregatedSectionScoreDto> calculateAggregatedSectionScores(
            List<FormSubmission> submissions, Form form) {

        // Get top-level sections
        List<FormSection> topLevelSections = form.getSections().stream()
                .filter(s -> s.getParentSection() == null && !s.isDeleted())
                .collect(Collectors.toList());

        List<AggregatedSectionScoreDto> result = new ArrayList<>();

        // For each section, calculate aggregated scores across all submissions
        for (FormSection section : topLevelSections) {
            AggregatedSectionScoreDto aggregatedScore = calculateAggregatedSectionScore(
                    submissions, section);
            result.add(aggregatedScore);
        }

        return result;
    }

    private AggregatedSectionScoreDto calculateAggregatedSectionScore(
            List<FormSubmission> submissions, FormSection section) {

        AggregatedSectionScoreDto dto = new AggregatedSectionScoreDto();
        dto.setSectionUuid(section.getUuid());
        dto.setSectionTitle(section.getTitle());

        // Calculate max possible score the same way submissions do
        // If section has fixed maxScore, use it; otherwise calculate from fields
        double maxPossibleScore;
        if (section.getMaxScore() != null && section.getMaxScore() > 0) {
            maxPossibleScore = section.getMaxScore();
        } else {
            // Calculate max score from fields using the same logic as submission scoring
            maxPossibleScore = calculateSectionMaxPossibleFromFields(section);
        }
        dto.setMaxPossibleScore(maxPossibleScore);

        // Collect scores for this section across all submissions
        List<Double> sectionScores = new ArrayList<>();
        List<Double> sectionPercentages = new ArrayList<>();

        for (FormSubmission submission : submissions) {
            submission.getSectionScores().stream()
                    .filter(score -> score.getSection().getId().equals(section.getId()))
                    .findFirst()
                    .ifPresent(score -> {
                        if (score.getScore() != null) {
                            sectionScores.add(score.getScore());
                        }
                        if (score.getPercentage() != null) {
                            sectionPercentages.add(score.getPercentage());
                        }
                    });
        }

        // Calculate average score and percentage
        dto.setAverageScore(sectionScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        dto.setAveragePercentage(sectionPercentages.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));

        // Recursively process subsections
        if (section.getSubsections() != null && !section.getSubsections().isEmpty()) {
            List<AggregatedSectionScoreDto> subsectionScores = new ArrayList<>();

            for (FormSection subsection : section.getSubsections()) {
                if (!subsection.isDeleted()) {
                    AggregatedSectionScoreDto subsectionScore = calculateAggregatedSectionScore(submissions,
                            subsection);
                    subsectionScores.add(subsectionScore);
                }
            }

            dto.setSubsectionScores(subsectionScores);
        }

        return dto;
    }

    /**
     * Calculate max possible score for a section from its fields
     * Uses the same logic as submission scoring
     */
    private double calculateSectionMaxPossibleFromFields(FormSection section) {
        double maxPossible = 0;

        // Recursively calculate from subsections first
        if (section.getSubsections() != null) {
            for (FormSection subsection : section.getSubsections()) {
                if (!subsection.isDeleted()) {
                    maxPossible += calculateSectionMaxPossibleFromFields(subsection);
                }
            }
        }

        // Calculate from direct fields in this section
        if (section.getFields() != null) {
            for (FormField field : section.getFields()) {
                if (!field.isDeleted() && field.getOptions() != null && !field.getOptions().isEmpty()) {
                    // Only calculate if section doesn't have fixed max score (same as submission logic)
                    if (section.getMaxScore() == null) {
                        if (field.getFieldType().equals("select") || field.getFieldType().equals("radio")) {
                            // For single-select fields, take max option score
                            int maxOptionScore = field.getOptions().stream()
                                    .map(o -> o.getScore() != null ? o.getScore() : 0)
                                    .max(Integer::compare)
                                    .orElse(0);
                            maxPossible += maxOptionScore;
                        } else if (field.getFieldType().equals("rating")) {
                            // Rating fields contribute 5 points max
                            maxPossible += 5;
                        } else if (field.getFieldType().equals("checkbox")) {
                            // For multi-select checkbox, sum all option scores
                            int totalCheckboxScore = field.getOptions().stream()
                                    .map(o -> o.getScore() != null ? o.getScore() : 0)
                                    .mapToInt(Integer::intValue)
                                    .sum();
                            maxPossible += totalCheckboxScore;
                        }
                    }
                }
            }
        }

        // If section has a defined max score, override the calculated value
        if (section.getMaxScore() != null && section.getMaxScore() > 0) {
            return section.getMaxScore();
        }

        return maxPossible;
    }

    @Override
    public Page<FormSubmissionSummaryDto> getHotelSubmissions(UUID hotelUuid, Pageable pageable, String assessor) {
        log.info("Getting submissions for hotel {} with assessor filter: {}", hotelUuid, assessor);

        // Get the hotel
        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        // Find submissions for this hotel, optionally filtered by assessor
        Page<FormSubmission> submissions;
        if (assessor != null && !assessor.trim().isEmpty()) {
            // Filter by both hotel and assessor
            submissions = formSubmissionRepository.findByHotelIdAndAssessor(hotel.getId(), assessor, pageable);
        } else {
            // Filter by hotel only
            submissions = formSubmissionRepository.findByHotelId(hotel.getId(), pageable);
        }

        // Map to DTOs
        return submissions.map(submission -> {
            FormSubmissionSummaryDto dto = new FormSubmissionSummaryDto();
            dto.setUuid(submission.getUuid());
            dto.setId(submission.getId());
            dto.setSubmittedBy(submission.getSubmittedBy());
            dto.setSubmittedAt(submission.getSubmittedAt());
            dto.setTotalScore(submission.getTotalScore());
            dto.setMaxPossibleScore(submission.getMaxPossibleScore());
            dto.setPercentage(submission.getPercentage());

            // Set form information
            if (submission.getForm() != null) {
                dto.setFormUuid(submission.getForm().getUuid());
                dto.setFormName(submission.getForm().getName());
            }

            // Set hotel information
            if (submission.getHotel() != null) {
                dto.setHotelUuid(submission.getHotel().getUuid());
                dto.setHotelName(submission.getHotel().getName());

                if (submission.getHotel().getPropertyType() != null) {
                    dto.setPropertyTypeName(submission.getHotel().getPropertyType().name());
                }
            }

            return dto;
        });
    }

    /**
     * Validates that section max scores are consistent with subsection max scores
     */
    private void validateSectionHierarchy(Set<FormSection> sections) {
        if (sections == null || sections.isEmpty()) {
            return;
        }

        for (FormSection section : sections) {
            if (section.getMaxScore() != null && section.getSubsections() != null
                    && !section.getSubsections().isEmpty()) {
                double subsectionMaxSum = section.getSubsections().stream()
                        .filter(s -> s.getMaxScore() != null && !s.isDeleted())
                        .mapToDouble(FormSection::getMaxScore)
                        .sum();

                if (subsectionMaxSum > section.getMaxScore()) {
                    throw new ValidationException("Sum of subsection maximum scores (" + subsectionMaxSum +
                            ") exceeds parent section maximum score (" + section.getMaxScore() +
                            ") for section: " + section.getTitle());
                }
            }

            // Recursively validate subsections
            if (section.getSubsections() != null && !section.getSubsections().isEmpty()) {
                validateSectionHierarchy(section.getSubsections());
            }
        }
    }

    @Override
    public boolean existsByFormUuidAndHotelUuidAndSubmittedBy(UUID formUuid, UUID hotelUuid, String submittedBy) {
        return formSubmissionRepository.existsByFormUuidAndHotelUuidAndSubmittedBy(formUuid, hotelUuid, submittedBy);
    }

    @Override
    public boolean existsByFormUuidAndHotelUuidAndAssessorId(UUID formUuid, UUID hotelUuid, Long assessorId) {
        return formSubmissionRepository.existsByFormUuidAndHotelUuidAndAssessorId(formUuid, hotelUuid, assessorId);
    }

    @Override
    public boolean checkDuplicateSubmission(UUID formUuid, UUID hotelUuid) {
        // Get the currently logged-in user
        LoggedInUserDto loggedInUser = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not authenticated"));

        // Find the assessor for the current user
        Assessor assessor = assessorRepository.findByUserEmail(loggedInUser.getEmail())
                .orElseThrow(() -> new ValidationException("Current user is not an assessor"));

        // Check if this assessor has already submitted this form for this hotel
        return existsByFormUuidAndHotelUuidAndAssessorId(formUuid, hotelUuid, assessor.getId());
    }

    @Override
    public boolean hasApprovedSubmission(UUID formUuid, UUID hotelUuid) {
        // Get the currently logged-in user
        LoggedInUserDto loggedInUser = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not authenticated"));

        // Check if this user has an APPROVED submission for this form and hotel
        return formSubmissionRepository.existsApprovedSubmission(formUuid, hotelUuid, loggedInUser.getEmail());
    }

    @Override
    public Optional<FormSubmission> findExistingDraft(UUID formUuid, UUID hotelUuid) {
        // Get the currently logged-in user
        LoggedInUserDto loggedInUser = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not authenticated"));

        log.info("Checking for existing DRAFT submission: form={}, hotel={}, user={}",
                formUuid, hotelUuid, loggedInUser.getEmail());

        // Find existing DRAFT for this user, form, and hotel
        Optional<FormSubmission> existingDraft = formSubmissionRepository.findExistingDraft(
                formUuid, hotelUuid, loggedInUser.getEmail());

        if (existingDraft.isPresent()) {
            log.info("Found existing DRAFT submission with UUID: {}", existingDraft.get().getUuid());
        } else {
            log.info("No existing DRAFT submission found");
        }

        return existingDraft;
    }

    @Override
    @Transactional
    public SubmissionAsDraftDto findLatestSubmissionForCurrentAssessor(UUID formUuid, UUID hotelUuid) {
        // Get the currently logged-in user
        LoggedInUserDto loggedInUser = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not authenticated"));

        log.info("Finding latest submission for variance resolution: form={}, hotel={}, user={}",
                formUuid, hotelUuid, loggedInUser.getEmail());

        // Find the assessor for the current user
        Assessor assessor = assessorRepository.findByUserEmail(loggedInUser.getEmail())
                .orElseThrow(() -> new ValidationException("Current user is not an assessor"));

        // Find all submissions for this assessor, form, and hotel (ordered by
        // submittedAt DESC)
        List<FormSubmission> submissions = formSubmissionRepository.findLatestByFormHotelAndAssessor(
                formUuid, hotelUuid, assessor.getId());

        if (submissions == null || submissions.isEmpty()) {
            log.warn("No submissions found for assessor {} on form {} and hotel {}",
                    assessor.getId(), formUuid, hotelUuid);
            return null;
        }

        // Get the most recent submission (first in list due to ORDER BY submittedAt
        // DESC)
        FormSubmission latestSubmission = submissions.get(0);

        log.info("Found latest submission with UUID: {}, submitted at: {}",
                latestSubmission.getUuid(), latestSubmission.getSubmittedAt());

        // Convert to draft-compatible format for variance resolution
        return convertSubmissionToDraftFormat(latestSubmission);
    }

    /**
     * Convert a FormSubmission to a draft-compatible format for variance
     * resolution.
     * This ensures the frontend can load submission data the same way it loads
     * draft data.
     */
    private SubmissionAsDraftDto convertSubmissionToDraftFormat(
            FormSubmission submission) {
        SubmissionAsDraftDto dto = new SubmissionAsDraftDto();

        // Set basic properties
        dto.setId(submission.getId());
        dto.setUuid(submission.getUuid());
        dto.setSubmittedBy(submission.getSubmittedBy());
        dto.setLastSavedAt(submission.getSubmittedAt());

        // Set form info
        if (submission.getForm() != null) {
            dto.setFormUuid(submission.getForm().getUuid());
            dto.setFormName(submission.getForm().getName());
        }

        // Set hotel info
        if (submission.getHotel() != null) {
            dto.setHotelUuid(submission.getHotel().getUuid());
            dto.setHotelName(submission.getHotel().getName());
        }

        // Set scores
        dto.setCompletionPercentage(submission.getPercentage());

        // Convert responses to formData JSON string (matching draft format)
        try {
            java.util.Map<String, Object> formDataMap = new java.util.HashMap<>();
            java.util.Map<String, Object> formValues = new java.util.HashMap<>();

            // Extract form values from responses
            // IMPORTANT: Checkbox fields have multiple rows per field (one row per checked option)
            // We need to group them into arrays
            if (submission.getResponses() != null) {
                java.util.Map<String, java.util.List<Object>> checkboxGroups = new java.util.HashMap<>();

                for (var response : submission.getResponses()) {
                    if (response.getField() != null && response.getField().getUuid() != null) {
                        String fieldUuid = response.getField().getUuid().toString();
                        Object value = response.getValue();

                        // Check if this is a checkbox field (multiple responses possible)
                        // Note: fieldType is stored in lowercase in the database
                        if ("checkbox".equalsIgnoreCase(response.getField().getFieldType())) {
                            // Group checkbox values together
                            checkboxGroups.computeIfAbsent(fieldUuid, k -> new java.util.ArrayList<>()).add(value);
                            log.debug("Grouped checkbox value for field {}: {}", fieldUuid, value);
                        } else {
                            // For non-checkbox fields, store value directly
                            formValues.put(fieldUuid, value);
                        }
                    }
                }

                // Add checkbox groups as arrays
                for (var entry : checkboxGroups.entrySet()) {
                    formValues.put(entry.getKey(), entry.getValue());
                    log.info("Checkbox field {} has {} values: {}", entry.getKey(), entry.getValue().size(), entry.getValue());
                }
            }
            formDataMap.put("formValues", formValues);

            // Extract section scores
            if (submission.getSectionScores() != null) {
                java.util.List<java.util.Map<String, Object>> sectionScoresList = new java.util.ArrayList<>();
                for (var score : submission.getSectionScores()) {
                    java.util.Map<String, Object> scoreMap = new java.util.HashMap<>();
                    if (score.getSection() != null) {
                        scoreMap.put("sectionUuid", score.getSection().getUuid());
                    }
                    scoreMap.put("score", score.getScore());
                    scoreMap.put("maxPossible", score.getMaxPossible());
                    scoreMap.put("percentage", score.getPercentage());
                    sectionScoresList.add(scoreMap);
                }
                formDataMap.put("sectionScores", sectionScoresList);
            }

            // Convert to JSON string
            String formDataJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(formDataMap);
            dto.setFormData(formDataJson);

            log.info("Converted submission {} to draft-compatible format with {} form values",
                    submission.getUuid(), formValues.size());
            log.debug("FormData JSON: {}", formDataJson);

        } catch (Exception e) {
            log.error("Error converting submission to draft format", e);
        }

        return dto;
    }

    @Override
    @Transactional
    public FormSubmissionResponseDto submitForApproval(UUID submissionUuid) {
        log.info("Submitting form submission {} for approval", submissionUuid);

        FormSubmission submission = formSubmissionRepository.findByUuid(submissionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with UUID " + submissionUuid + " not found"));

        // Validate that submission is in DRAFT/REJECTED status
        SubmissionStatus status = submission.getStatus();

        if (status != SubmissionStatus.DRAFT && status != SubmissionStatus.REJECTED) {
            throw new ValidationException(
                    "Only DRAFT or REJECTED submissions can be submitted for approval. Current status: " + status);
        }

        // IMPORTANT: Delete any other submissions from this assessor for the same form+hotel
        // This ensures only ONE submission per (form, hotel, assessor) combination
        if (submission.getForm() != null && submission.getHotel() != null && submission.getAssessor() != null) {
            UUID formUuid = submission.getForm().getUuid();
            UUID hotelUuid = submission.getHotel().getUuid();
            Long assessorId = submission.getAssessor().getId();

            log.info("Checking for existing submissions from assessor {} for form {} and hotel {}",
                    assessorId, formUuid, hotelUuid);

            // Find all submissions from this assessor for same form+hotel
            java.util.List<FormSubmission> existingSubmissions = formSubmissionRepository
                    .findLatestByFormHotelAndAssessor(formUuid, hotelUuid, assessorId);

            // Filter out the current submission being submitted
            existingSubmissions = existingSubmissions.stream()
                    .filter(s -> !s.getUuid().equals(submissionUuid))
                    .collect(java.util.stream.Collectors.toList());

            if (!existingSubmissions.isEmpty()) {
                log.info("Found {} existing submission(s) from this assessor. Deleting them to ensure only one submission exists...",
                        existingSubmissions.size());

                for (FormSubmission oldSubmission : existingSubmissions) {
                    log.info("Deleting old submission {} (status: {})",
                            oldSubmission.getUuid(), oldSubmission.getStatus());
                    oldSubmission.setDeleted(true);
                    formSubmissionRepository.save(oldSubmission);
                }

                log.info("Successfully deleted {} old submission(s). Only the latest submission will be retained.",
                        existingSubmissions.size());
            } else {
                log.info("No existing submissions found. This is the first submission for this assessor on this form and hotel.");
            }
        }

        // Update status to SUBMITTED
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedForApprovalAt(LocalDateTime.now());

        formSubmissionRepository.save(submission);

        log.info("Form submission {} submitted for approval", submissionUuid);
        return new FormSubmissionResponseDto(submission).withDetailsAndScores(submission);
    }

    @Override
    @Transactional
    public FormSubmissionResponseDto approveSubmission(UUID submissionUuid) {
        log.info("Approving form submission {}", submissionUuid);

        FormSubmission submission = formSubmissionRepository.findByUuid(submissionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with UUID " + submissionUuid + " not found"));

        // Validate that submission is in SUBMITTED status
        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new ValidationException(
                    "Only SUBMITTED submissions can be approved. Current status: " + submission.getStatus());
        }

        // Update status to APPROVED
        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setApprovedAt(LocalDateTime.now());
        submission.setRejectedAt(null);
        submission.setRejectionReason(null);

        formSubmissionRepository.save(submission);

        log.info("Form submission {} approved", submissionUuid);
        return new FormSubmissionResponseDto(submission).withDetailsAndScores(submission);
    }

    @Override
    @Transactional
    public FormSubmissionResponseDto rejectSubmission(UUID submissionUuid, String rejectionReason) {
        log.info("Rejecting form submission {} with reason: {}", submissionUuid, rejectionReason);

        FormSubmission submission = formSubmissionRepository.findByUuid(submissionUuid)
                .orElseThrow(() -> new ValidationException(
                        "Form submission with UUID " + submissionUuid + " not found"));

        // Validate that submission is in SUBMITTED status
        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new ValidationException(
                    "Only SUBMITTED submissions can be rejected. Current status: " + submission.getStatus());
        }

        // Validate that rejection reason is provided
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new ValidationException("Rejection reason is required");
        }

        // Update status to REJECTED
        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setRejectedAt(LocalDateTime.now());
        submission.setRejectionReason(rejectionReason);
        submission.setApprovedAt(null);

        formSubmissionRepository.save(submission);

        log.info("Form submission {} rejected", submissionUuid);
        return new FormSubmissionResponseDto(submission).withDetailsAndScores(submission);
    }

    @Override
    public Page<FormSubmissionResponseDto> getMySubmissions(Pageable pageable) {
        log.info("Getting submissions for currently authenticated assessor");

        // Get the current user
        LoggedInUserDto loggedInUser = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not authenticated"));

        // Find the assessor associated with this user
        Assessor assessor = assessorRepository.findByUserEmail(loggedInUser.getEmail())
                .orElseThrow(() -> new ValidationException("Current user is not an assessor"));

        log.info("Found assessor {} for user {}", assessor.getId(), loggedInUser.getEmail());

        // Get ALL submissions for this assessor (unpaginated)
        List<FormSubmission> allSubmissions = formSubmissionRepository
                .findByAssessorIdOrderBySubmittedAtDesc(assessor.getId());

        log.info("Found {} total submissions for assessor {}", allSubmissions.size(), assessor.getId());

        // Group submissions by hotel + form combination, keeping only the one with highest percentage
        Map<String, FormSubmission> highestPercentageSubmissions = new java.util.HashMap<>();

        for (FormSubmission submission : allSubmissions) {
            // Create a composite key: hotelId + formId
            String key = submission.getHotel().getId() + "_" + submission.getForm().getId();

            // Get existing submission for this hotel/form combo
            FormSubmission existing = highestPercentageSubmissions.get(key);

            // Keep the submission with highest percentage
            if (existing == null ||
                (submission.getPercentage() != null &&
                 (existing.getPercentage() == null || submission.getPercentage() > existing.getPercentage()))) {
                highestPercentageSubmissions.put(key, submission);
                log.debug("Updated highest percentage submission for hotel {} and form {}: {} ({}%)",
                        submission.getHotel().getName(), submission.getForm().getName(),
                        submission.getUuid(), submission.getPercentage());
            }
        }

        // Convert map values to list
        List<FormSubmission> filteredSubmissions = new java.util.ArrayList<>(highestPercentageSubmissions.values());

        log.info("Filtered to {} unique hotel/form combinations with highest percentages",
                filteredSubmissions.size());

        // Sort by submitted date (most recent first)
        filteredSubmissions.sort((a, b) -> {
            if (a.getSubmittedAt() == null) return 1;
            if (b.getSubmittedAt() == null) return -1;
            return b.getSubmittedAt().compareTo(a.getSubmittedAt());
        });

        // Manually paginate the filtered list
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredSubmissions.size());

        List<FormSubmission> pageContent = start < filteredSubmissions.size()
                ? filteredSubmissions.subList(start, end)
                : new java.util.ArrayList<>();

        // Convert to DTOs
        List<FormSubmissionResponseDto> dtos = pageContent.stream()
                .map(submission -> {
                    FormSubmissionResponseDto dto = new FormSubmissionResponseDto(submission)
                            .withDetailsAndScores(submission);

                    // Populate assessor count information for multi-assessor validation
                    if (submission.getHotel() != null && submission.getForm() != null) {
                        populateAssessorCounts(dto, submission);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // Create a Page object with the filtered and paginated results
        return new PageImpl<>(dtos, pageable, filteredSubmissions.size());
    }

    /**
     * Update the has_unresolved_variances and varianceCheckStatus flags for ALL
     * submissions of a hotel/form.
     * This ensures consistency when new assessments create or update variances.
     *
     * CRITICAL: When a 2nd or 3rd assessor submits, this method updates ALL
     * previous submissions
     * with the correct variance flags, not just the current submission.
     *
     * @param hotelId The hotel ID
     * @param formId  The form ID
     */
    private void updateAllSubmissionsVarianceFlag(Long hotelId, Long formId) {
        try {
            log.info("Updating variance flags for all submissions: hotel={}, form={}", hotelId, formId);

            // Get all submissions for this hotel/form (including the current one)
            List<FormSubmission> allSubmissions = formSubmissionRepository
                    .findByHotelIdAndFormIdWithScores(hotelId, formId);

            if (allSubmissions.isEmpty()) {
                log.warn("No submissions found for hotel={}, form={}", hotelId, formId);
                return;
            }

            // Check if there are unresolved variances for this hotel/form
            var unresolvedVariances = assessmentVarianceLogService
                    .getUnresolvedVariances(hotelId, formId);

            boolean hasVariances = unresolvedVariances != null && !unresolvedVariances.isEmpty();

            log.info("Found {} unresolved variances for hotel={}, form={}",
                    hasVariances ? unresolvedVariances.size() : 0, hotelId, formId);

            // Update ALL submissions with the correct flags
            for (FormSubmission submission : allSubmissions) {
                submission.setHasUnresolvedVariances(hasVariances);
                submission.setVarianceCheckStatus(hasVariances ? "HAS_VARIANCE" : "NO_VARIANCE");
            }

            // Batch save all updated submissions
            formSubmissionRepository.saveAll(allSubmissions);

            log.info("Updated {} submissions with has_unresolved_variances={}, varianceCheckStatus={}",
                    allSubmissions.size(), hasVariances, hasVariances ? "HAS_VARIANCE" : "NO_VARIANCE");

        } catch (Exception e) {
            log.error("Error updating variance flags for hotel={}, form={}: {}",
                    hotelId, formId, e.getMessage(), e);
            // Don't fail the main transaction - variance flag updates are non-critical
        }
    }

    /**
     * Populates assessor count information for multi-assessor validation.
     * This enables the frontend to determine if all required assessors have
     * submitted
     * before allowing submission for approval.
     *
     * @param dto        The DTO to populate
     * @param submission The form submission entity
     */
    private void populateAssessorCounts(FormSubmissionResponseDto dto, FormSubmission submission) {
        try {
            // Get all submissions for this hotel and form (including DRAFT)
            List<FormSubmission> allSubmissions = formSubmissionRepository
                    .findByHotelIdAndFormIdWithScores(
                            submission.getHotel().getId(),
                            submission.getForm().getId());

            // Count distinct assessors who have submitted
            long distinctAssessorCount = allSubmissions.stream()
                    .filter(s -> s.getAssessor() != null)
                    .map(s -> s.getAssessor().getId())
                    .distinct()
                    .count();

            // Set total required assessors (currently hardcoded to 3 for variance
            // detection)
            // TODO: This should ideally come from hotel/form configuration
            dto.setTotalRequiredAssessors(3);
            dto.setSubmittedAssessorCount((int) distinctAssessorCount);

        } catch (Exception e) {
            log.warn("Error populating assessor counts for submission {}: {}",
                    submission.getUuid(), e.getMessage());
            // Don't fail - set defaults
            dto.setTotalRequiredAssessors(3);
            dto.setSubmittedAssessorCount(0);
        }
    }
}
