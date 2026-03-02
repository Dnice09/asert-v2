package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldResponseDto;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldResponseItemDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.entity.FormSubmissionScore;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionResponseDto {
    private Long id;
    private UUID uuid;
    private FormResponseDto form;
    private String submittedBy;
    private String status;
    private String varianceCheckStatus;
    private Boolean hasUnresolvedVariances;
    private HotelResponseDto hotel;
    private AssessorResponseDto assessor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;
    private List<SectionScoreDto> sectionScores;
    private List<FormFieldResponseItemDto> responses;

    // Assessor submission tracking for multi-assessor validation
    private Integer totalRequiredAssessors;
    private Integer submittedAssessorCount;

    public FormSubmissionResponseDto(FormSubmission entity) {
        entity.toDao(this);
    }

    /**
     * Enhances the DTO with details from the entity, including related entities
     *
     * @param submission The FormSubmission entity to map from
     * @return The enhanced DTO
     */
    public FormSubmissionResponseDto withDetails(FormSubmission submission) {
        // Copy basic properties
        BeanUtils.copyProperties(submission, this, "form", "responses", "sectionScores");

        // Set form with sections and fields
        if (submission.getForm() != null) {
            this.form = new FormResponseDto(submission.getForm());
        }

        status = submission.getStatus() != null ? submission.getStatus().name() : null;

        hotel = new HotelResponseDto(submission.getHotel());

        assessor = new AssessorResponseDto(submission.getAssessor());

        // Map responses
        if (submission.getResponses() != null && !submission.getResponses().isEmpty()) {
            this.responses = submission.getResponses().stream()
                    .map(response -> {
                        FormFieldResponseItemDto itemDto = new FormFieldResponseItemDto();

                        // Set basic properties
                        itemDto.setId(response.getId());
                        itemDto.setUuid(response.getUuid());
                        itemDto.setValue(response.getValue());

                        // Set field
                        if (response.getField() != null) {
                            itemDto.setField(new FormFieldResponseDto(response.getField()));
                        }

                        return itemDto;
                    })
                    .collect(Collectors.toList());
        }

        return this;
    }

    /**
     * Enhances the DTO with details from the entity, including related entities and
     * scores
     *
     * @param submission The FormSubmission entity to map from
     * @return The enhanced DTO
     */
    public FormSubmissionResponseDto withDetailsAndScores(FormSubmission submission) {
        // First, populate basic details
        withDetails(submission);

        // Set overall score information
        this.totalScore = submission.getTotalScore();
        this.maxPossibleScore = submission.getMaxPossibleScore();
        this.percentage = submission.getPercentage();

        // Map section scores if available
        if (submission.getSectionScores() != null && !submission.getSectionScores().isEmpty()) {
            // Create a map of section scores by section ID
            Map<Long, FormSubmissionScore> scoresBySection = submission.getSectionScores().stream()
                    .collect(Collectors.toMap(
                            score -> score.getSection().getId(),
                            score -> score));

            // Create a map of sections by ID for easy lookup
            Map<Long, FormSection> sectionsById = new HashMap<>();
            if (submission.getForm() != null && submission.getForm().getSections() != null) {
                submission.getForm().getSections().forEach(section -> sectionsById.put(section.getId(), section));
            }

            // Get top-level sections (those without parent)
            List<FormSection> topLevelSections = submission.getForm().getSections().stream()
                    .filter(section -> section.getParentSection() == null && !section.isDeleted())
                    .sorted((a, b) -> a.getOrderIndex() - b.getOrderIndex())
                    .collect(Collectors.toList());

            // Map top-level section scores
            this.sectionScores = topLevelSections.stream()
                    .map(section -> mapSectionScore(section, scoresBySection, sectionsById))
                    .collect(Collectors.toList());
        }

        return this;
    }

    /**
     * Recursively maps section scores to DTOs
     */
    private SectionScoreDto mapSectionScore(
            FormSection section,
            Map<Long, FormSubmissionScore> scoresBySection,
            Map<Long, FormSection> sectionsById) {

        SectionScoreDto sectionScoreDto = new SectionScoreDto();
        sectionScoreDto.setSectionId(section.getId());
        sectionScoreDto.setSectionUuid(section.getUuid());
        sectionScoreDto.setSectionTitle(section.getTitle());

        // Get score for this section if available
        FormSubmissionScore sectionScore = scoresBySection.get(section.getId());
        if (sectionScore != null) {
            sectionScoreDto.setScore(sectionScore.getScore());
            sectionScoreDto.setMaxPossible(sectionScore.getMaxPossible());
            sectionScoreDto.setPercentage(sectionScore.getPercentage());
        }

        // Process subsections recursively
        if (section.getSubsections() != null && !section.getSubsections().isEmpty()) {
            List<SectionScoreDto> subsectionScoreDtos = new ArrayList<>();

            // Sort subsections by order index
            section.getSubsections().stream()
                    .filter(s -> !s.isDeleted())
                    .sorted((a, b) -> a.getOrderIndex() - b.getOrderIndex())
                    .forEach(subsection -> {
                        SectionScoreDto subsectionScore = mapSectionScore(
                                subsection, scoresBySection, sectionsById);
                        subsectionScoreDtos.add(subsectionScore);
                    });

            sectionScoreDto.setSubsectionScores(subsectionScoreDtos);
        }

        return sectionScoreDto;
    }
}
