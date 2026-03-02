package tz.go.mnrt.asert.modules.selfassessment.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formfield.repository.FormFieldRepository;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmission.repository.FormSubmissionRepository;
import tz.go.mnrt.asert.modules.form.formsubmission.services.SectionScoreResult;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.ratingcriteria.services.RatingCriteriaService;
import tz.go.mnrt.asert.modules.selfassessment.dtos.FieldResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentComparisonDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentRequestDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentResultDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentSummaryDto;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessment;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentFieldResponse;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentScore;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentStatus;
import tz.go.mnrt.asert.modules.selfassessment.repository.SelfAssessmentFieldResponseRepository;
import tz.go.mnrt.asert.modules.selfassessment.repository.SelfAssessmentRepository;
import tz.go.mnrt.asert.modules.selfassessment.repository.SelfAssessmentScoreRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class SelfAssessmentServiceImpl implements SelfAssessmentService {

    private final SelfAssessmentRepository selfAssessmentRepository;
    private final SelfAssessmentScoreRepository selfAssessmentScoreRepository;
    private final SelfAssessmentFieldResponseRepository fieldResponseRepository;
    private final FormRepository formRepository;
    private final FormFieldRepository formFieldRepository;
    private final HotelRepository hotelRepository;
    private final RatingCriteriaService ratingCriteriaService;
    private final FormSubmissionRepository formSubmissionRepository;

    @Override
    @Transactional
    public SelfAssessmentResponseDto startSelfAssessment(SelfAssessmentRequestDto request) {
        log.info("Starting self-assessment for hotel: {}, form: {}", request.getHotelUuid(), request.getFormUuid());

        // Validate hotel
        Hotel hotel = hotelRepository.findByUuid(request.getHotelUuid())
                .orElseThrow(() -> new ValidationException("Hotel not found"));

        // Validate form
        Form form = formRepository.findByUuid(request.getFormUuid())
                .orElseThrow(() -> new ValidationException("Form not found"));

        // Validate hotel type matches form
        if (!form.getPropertyTypes().contains(hotel.getPropertyType())) {
            throw new ValidationException("This form cannot be used for this type of hotel. " +
                    "Form supports types: " + form.getPropertyTypes() + ", Hotel type: " + hotel.getPropertyType());
        }

        // Get current user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Create new self-assessment draft
        SelfAssessment selfAssessment = SelfAssessment.builder()
                .hotel(hotel)
                .form(form)
                .submittedBy(username)
                .status(SelfAssessmentStatus.DRAFT)
                .isOfficial(false)
                .build();
        selfAssessment.setCreatedAt(LocalDateTime.now());

        selfAssessmentRepository.save(selfAssessment);

        log.info("Created self-assessment draft with UUID: {}", selfAssessment.getUuid());
        return new SelfAssessmentResponseDto(selfAssessment);
    }

    @Override
    @Transactional
    public SelfAssessmentResponseDto saveDraft(UUID uuid, List<FieldResponseDto> responses) {
        log.info("Saving draft for self-assessment: {}", uuid);

        // Find self-assessment using optimized query (fetch hotel and form only)
        SelfAssessment selfAssessment = selfAssessmentRepository.findByUuidWithHotelAndForm(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment not found"));

        // Validate ownership
        validateOwnership(selfAssessment);

        // Validate status is DRAFT
        if (selfAssessment.getStatus() != SelfAssessmentStatus.DRAFT) {
            throw new ValidationException("Cannot modify a completed self-assessment");
        }

        // Delete existing responses
        fieldResponseRepository.deleteBySelfAssessmentId(selfAssessment.getId());

        // Save new responses
        for (FieldResponseDto responseDto : responses) {
            FormField field = formFieldRepository.findByUuid(responseDto.getFieldUuid())
                    .orElseThrow(() -> new ValidationException("Field not found: " + responseDto.getFieldUuid()));

            SelfAssessmentFieldResponse response = SelfAssessmentFieldResponse.builder()
                    .selfAssessment(selfAssessment)
                    .field(field)
                    .value(responseDto.getValue())
                    .build();
            response.setCreatedAt(LocalDateTime.now());

            fieldResponseRepository.save(response);
        }

        selfAssessment.setUpdatedAt(LocalDateTime.now());
        selfAssessmentRepository.save(selfAssessment);

        log.info("Saved {} responses for self-assessment: {}", responses.size(), uuid);
        return new SelfAssessmentResponseDto(selfAssessment);
    }

    @Override
    @Transactional
    public SelfAssessmentResponseDto submitSelfAssessment(UUID uuid) {
        log.info("Submitting self-assessment: {}", uuid);

        // Find self-assessment using optimized query
        SelfAssessment selfAssessment = selfAssessmentRepository.findByUuidWithHotelAndForm(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment not found"));

        // Validate ownership
        validateOwnership(selfAssessment);

        // Validate status is DRAFT
        if (selfAssessment.getStatus() != SelfAssessmentStatus.DRAFT) {
            throw new ValidationException("Self-assessment already submitted");
        }

        // Calculate scores (using same logic as FormSubmissionServiceImpl)
        calculateAndSaveScores(selfAssessment);

        // Calculate estimated star rating
        if (selfAssessment.getTotalScore() != null && selfAssessment.getTotalScore() > 0) {
            String estimatedRating = ratingCriteriaService.getTopStarRatingByPropertyTypeAndScore(
                    selfAssessment.getTotalScore(),
                    selfAssessment.getHotel().getPropertyType());
            selfAssessment.setEstimatedRating(estimatedRating);
        }

        // Update status and submission time
        selfAssessment.setStatus(SelfAssessmentStatus.COMPLETED);
        selfAssessment.setSubmittedAt(LocalDateTime.now());
        selfAssessment.setUpdatedAt(LocalDateTime.now());

        selfAssessmentRepository.save(selfAssessment);

        log.info("Self-assessment submitted. Score: {} / {} ({}%)",
                selfAssessment.getTotalScore(),
                selfAssessment.getMaxPossibleScore(),
                selfAssessment.getPercentage());

        return new SelfAssessmentResponseDto(selfAssessment);
    }

    @Override
    public List<SelfAssessmentSummaryDto> getHotelSelfAssessments(UUID hotelUuid) {
        log.info("Getting self-assessments for hotel: {}", hotelUuid);

        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel not found"));

        List<SelfAssessment> assessments = selfAssessmentRepository.findByHotelIdAndIsDeletedFalseOrderByCreatedAtDesc(hotel.getId());

        return assessments.stream()
                .map(SelfAssessmentSummaryDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public SelfAssessmentResultDto getSelfAssessmentResult(UUID uuid) {
        log.info("Getting result for self-assessment: {}", uuid);

        // Use optimized queries
        SelfAssessment selfAssessment = selfAssessmentRepository.findByUuidWithHotelAndForm(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment not found"));

        // Validate ownership
        validateOwnership(selfAssessment);

        // Load section scores separately
        selfAssessmentRepository.findByUuidWithSectionScores(uuid);

        SelfAssessmentResultDto result = new SelfAssessmentResultDto();
        result.setUuid(selfAssessment.getUuid());
        result.setHotelName(selfAssessment.getHotel().getName());
        result.setFormName(selfAssessment.getForm().getName());
        result.setSubmittedBy(selfAssessment.getSubmittedBy());
        result.setSubmittedAt(selfAssessment.getSubmittedAt());
        result.setTotalScore(selfAssessment.getTotalScore());
        result.setMaxPossibleScore(selfAssessment.getMaxPossibleScore());
        result.setPercentage(selfAssessment.getPercentage());
        result.setEstimatedRating(selfAssessment.getEstimatedRating());
        result.setCreatedAt(selfAssessment.getCreatedAt());

        // Get section scores
        List<SectionScoreDto> sectionScores = getSectionScores(selfAssessment);
        result.setSectionScores(sectionScores);

        return result;
    }

    @Override
    @Transactional
    public void deleteSelfAssessment(UUID uuid) {
        log.info("Deleting self-assessment: {}", uuid);

        SelfAssessment selfAssessment = selfAssessmentRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment not found"));

        // Validate ownership
        validateOwnership(selfAssessment);

        // Soft delete
        selfAssessment.setDeleted(true);
        selfAssessment.setUpdatedAt(LocalDateTime.now());
        selfAssessmentRepository.save(selfAssessment);

        log.info("Self-assessment deleted: {}", uuid);
    }

    @Override
    public SelfAssessmentComparisonDto compareSelfAndOfficialAssessments(UUID hotelUuid) {
        log.info("Comparing self-assessment with official assessment for hotel: {}", hotelUuid);

        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel not found"));

        SelfAssessmentComparisonDto comparison = new SelfAssessmentComparisonDto();
        comparison.setHotelName(hotel.getName());

        // Get latest completed self-assessment
        SelfAssessment latestSelfAssessment = selfAssessmentRepository
                .findLatestCompletedByHotelUuid(hotelUuid)
                .orElse(null);

        if (latestSelfAssessment != null) {
            comparison.setLatestSelfAssessment(getSelfAssessmentResult(latestSelfAssessment.getUuid()));
        }

        // Get official assessment
        List<FormSubmission> officialSubmissions = formSubmissionRepository
                .findByHotelIdOrderBySubmittedAtDesc(hotel.getId());

        if (!officialSubmissions.isEmpty()) {
            FormSubmission official = officialSubmissions.get(0);
            SelfAssessmentComparisonDto.OfficialAssessmentDto officialDto =
                    new SelfAssessmentComparisonDto.OfficialAssessmentDto();
            officialDto.setTotalScore(official.getTotalScore());
            officialDto.setPercentage(official.getPercentage());

            // Get star rating
            if (official.getTotalScore() != null) {
                String rating = ratingCriteriaService.getTopStarRatingByPropertyTypeAndScore(
                        official.getTotalScore(),
                        hotel.getPropertyType());
                officialDto.setStarRating(rating);
            }

            comparison.setOfficialAssessment(officialDto);

            // Calculate variance
            if (latestSelfAssessment != null && latestSelfAssessment.getPercentage() != null
                    && official.getPercentage() != null) {
                double variance = Math.abs(latestSelfAssessment.getPercentage() - official.getPercentage());
                comparison.setVariance(variance);
            }
        }

        return comparison;
    }

    @Override
    @Transactional
    public SelfAssessmentResponseDto recalculateScores(UUID uuid) {
        log.info("Recalculating scores for self-assessment: {}", uuid);

        SelfAssessment selfAssessment = selfAssessmentRepository.findByUuidWithHotelAndForm(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment not found"));

        // Delete existing scores
        selfAssessmentScoreRepository.deleteBySelfAssessmentId(selfAssessment.getId());

        // Reset scores
        selfAssessment.setTotalScore(null);
        selfAssessment.setMaxPossibleScore(null);
        selfAssessment.setPercentage(null);

        // Recalculate
        calculateAndSaveScores(selfAssessment);

        return new SelfAssessmentResponseDto(selfAssessment);
    }

    /**
     * CRITICAL: This method uses the EXACT SAME LOGIC as FormSubmissionServiceImpl.calculateAndSaveScores()
     * to ensure self-assessment scores match official assessment calculations
     */
    private void calculateAndSaveScores(SelfAssessment selfAssessment) {
        log.info("Calculating scores for self-assessment: {}", selfAssessment.getUuid());

        // Load field responses separately (optimized)
        selfAssessmentRepository.findByUuidWithFieldResponses(selfAssessment.getUuid());

        // Get all responses for this self-assessment
        List<SelfAssessmentFieldResponse> responses = fieldResponseRepository
                .findBySelfAssessmentIdWithField(selfAssessment.getId());

        // Group responses by field ID for quick lookup
        Map<Long, List<SelfAssessmentFieldResponse>> responsesByFieldId = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getField().getId()));

        // Get top-level sections for the form
        List<FormSection> topLevelSections = selfAssessment.getForm().getSections().stream()
                .filter(s -> s.getParentSection() == null && !s.isDeleted())
                .collect(Collectors.toList());

        double totalScore = 0;
        double totalMaxPossible = 0;

        // Calculate score for each top-level section
        for (FormSection section : topLevelSections) {
            SectionScoreResult sectionScore = calculateSectionScore(section, responsesByFieldId);
            totalScore += sectionScore.getScore();
            totalMaxPossible += sectionScore.getMaxPossible();

            // Save section score
            saveSectionScore(selfAssessment, section, sectionScore);
        }

        // Save total score
        selfAssessment.setTotalScore(totalScore);
        selfAssessment.setMaxPossibleScore(totalMaxPossible);
        selfAssessment.setPercentage(totalMaxPossible > 0 ? (totalScore / totalMaxPossible) * 100 : 0);
        selfAssessmentRepository.save(selfAssessment);
    }

    /**
     * CRITICAL: This is a direct copy of FormSubmissionServiceImpl.calculateSectionScore()
     * DO NOT modify this logic - it must match official assessment calculation exactly
     */
    private SectionScoreResult calculateSectionScore(FormSection section,
            Map<Long, List<SelfAssessmentFieldResponse>> responsesByFieldId) {
        double sectionScore = 0;
        double sectionMaxPossible = 0;
        List<SectionScoreResult> subsectionScores = new ArrayList<>();

        // Calculate scores for subsections first (recursive)
        List<FormSection> subsections = section.getSubsections().stream()
                .filter(s -> !s.isDeleted())
                .collect(Collectors.toList());

        for (FormSection subsection : subsections) {
            SectionScoreResult subsectionScore = calculateSectionScore(subsection, responsesByFieldId);
            sectionScore += subsectionScore.getScore();
            sectionMaxPossible += subsectionScore.getMaxPossible();
            subsectionScores.add(subsectionScore);
        }

        // Calculate scores for direct fields in this section
        List<FormField> fields = section.getFields().stream()
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());

        for (FormField field : fields) {
            List<SelfAssessmentFieldResponse> responses = responsesByFieldId.get(field.getId());
            if (responses != null && !responses.isEmpty()) {
                // For checkbox fields - handle multiple separate responses
                if ("checkbox".equals(field.getFieldType())) {
                    for (SelfAssessmentFieldResponse response : responses) {
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
                    // For single-value fields (select, radio, rating)
                    SelfAssessmentFieldResponse response = responses.get(0);

                    if ("select".equals(field.getFieldType()) || "radio".equals(field.getFieldType())) {
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
                    if ("rating".equals(field.getFieldType())) {
                        try {
                            int rating = Integer.parseInt(response.getValue());
                            sectionScore += rating;
                        } catch (NumberFormatException e) {
                            log.error("Error parsing rating value: {}", response.getValue());
                        }
                    }
                }
            }

            // Calculate max possible score for this field
            if (section.getMaxScore() == null) {
                if ("select".equals(field.getFieldType()) || "radio".equals(field.getFieldType())) {
                    int maxOptionScore = field.getOptions().stream()
                            .map(o -> o.getScore() != null ? o.getScore() : 0)
                            .max(Integer::compare)
                            .orElse(0);
                    sectionMaxPossible += maxOptionScore;
                } else if ("rating".equals(field.getFieldType())) {
                    sectionMaxPossible += 5; // Assuming rating scale is 1-5
                } else if ("checkbox".equals(field.getFieldType())) {
                    int totalCheckboxScore = field.getOptions().stream()
                            .map(o -> o.getScore() != null ? o.getScore() : 0)
                            .mapToInt(Integer::intValue)
                            .sum();
                    sectionMaxPossible += totalCheckboxScore;
                }
            }
        }

        // If section has a defined max score, use that instead
        if (section.getMaxScore() != null && section.getMaxScore() > 0) {
            sectionMaxPossible = section.getMaxScore();
        }

        return new SectionScoreResult(section, sectionScore, sectionMaxPossible, subsectionScores);
    }

    private void saveSectionScore(SelfAssessment selfAssessment, FormSection section, SectionScoreResult scoreResult) {
        SelfAssessmentScore score = SelfAssessmentScore.builder()
                .selfAssessment(selfAssessment)
                .section(section)
                .score(scoreResult.getScore())
                .maxPossible(scoreResult.getMaxPossible())
                .percentage(scoreResult.getMaxPossible() > 0 ? (scoreResult.getScore() / scoreResult.getMaxPossible()) * 100 : 0)
                .build();
        score.setCreatedAt(LocalDateTime.now());

        selfAssessmentScoreRepository.save(score);

        // Save subsection scores (recursive)
        for (SectionScoreResult subsectionResult : scoreResult.getSubsectionScores()) {
            saveSectionScore(selfAssessment, subsectionResult.getSection(), subsectionResult);
        }
    }

    private List<SectionScoreDto> getSectionScores(SelfAssessment selfAssessment) {
        List<SelfAssessmentScore> scores = selfAssessmentScoreRepository
                .findBySelfAssessmentIdWithSection(selfAssessment.getId());

        // Get only top-level section scores (sections with no parent)
        return scores.stream()
                .filter(score -> score.getSection().getParentSection() == null)
                .map(this::toSectionScoreDto)
                .collect(Collectors.toList());
    }

    private SectionScoreDto toSectionScoreDto(SelfAssessmentScore score) {
        SectionScoreDto dto = new SectionScoreDto();
        dto.setSectionId(score.getSection().getId());
        dto.setSectionUuid(score.getSection().getUuid());
        dto.setSectionTitle(score.getSection().getTitle());
        dto.setScore(score.getScore());
        dto.setMaxPossible(score.getMaxPossible());
        dto.setPercentage(score.getPercentage());

        // Get subsection scores
        List<SelfAssessmentScore> subsectionScores = selfAssessmentScoreRepository
                .findBySelfAssessmentIdAndIsDeletedFalse(score.getSelfAssessment().getId())
                .stream()
                .filter(s -> s.getSection().getParentSection() != null &&
                        s.getSection().getParentSection().getId().equals(score.getSection().getId()))
                .collect(Collectors.toList());

        if (!subsectionScores.isEmpty()) {
            dto.setSubsectionScores(subsectionScores.stream()
                    .map(this::toSectionScoreDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private void validateOwnership(SelfAssessment selfAssessment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!selfAssessment.getSubmittedBy().equals(username)) {
            throw new ValidationException("You do not have permission to access this self-assessment");
        }
    }
}
