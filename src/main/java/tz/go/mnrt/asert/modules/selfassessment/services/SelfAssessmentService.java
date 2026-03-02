package tz.go.mnrt.asert.modules.selfassessment.services;

import java.util.List;
import java.util.UUID;

import tz.go.mnrt.asert.modules.selfassessment.dtos.FieldResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentComparisonDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentRequestDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentResultDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentSummaryDto;

public interface SelfAssessmentService {

    /**
     * Start a new self-assessment draft
     */
    SelfAssessmentResponseDto startSelfAssessment(SelfAssessmentRequestDto request);

    /**
     * Save draft responses (partial completion allowed)
     */
    SelfAssessmentResponseDto saveDraft(UUID uuid, List<FieldResponseDto> responses);

    /**
     * Submit completed self-assessment and calculate scores
     */
    SelfAssessmentResponseDto submitSelfAssessment(UUID uuid);

    /**
     * Get all self-assessments for a hotel
     */
    List<SelfAssessmentSummaryDto> getHotelSelfAssessments(UUID hotelUuid);

    /**
     * Get detailed result for a specific self-assessment
     */
    SelfAssessmentResultDto getSelfAssessmentResult(UUID uuid);

    /**
     * Delete a self-assessment
     */
    void deleteSelfAssessment(UUID uuid);

    /**
     * Compare latest self-assessment with official assessment
     */
    SelfAssessmentComparisonDto compareSelfAndOfficialAssessments(UUID hotelUuid);

    /**
     * Recalculate scores for a self-assessment (if needed)
     */
    SelfAssessmentResponseDto recalculateScores(UUID uuid);
}
