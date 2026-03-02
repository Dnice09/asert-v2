package tz.go.mnrt.asert.modules.selfassessment.rest;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.selfassessment.dtos.FieldResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentRequestDto;
import tz.go.mnrt.asert.modules.selfassessment.services.SelfAssessmentService;

@RestController
@RequestMapping(Constant.API_V1 + "/self-assessments")
@RequiredArgsConstructor
@Slf4j
public class SelfAssessmentResource {

    private final SelfAssessmentService selfAssessmentService;

    /**
     * Start a new self-assessment draft
     * POST /api/v1/self-assessments/start
     */
    @PostMapping("/start")
    public CustomApiResponse startSelfAssessment(@Valid @RequestBody SelfAssessmentRequestDto request) {
        log.info("REST: Starting self-assessment for hotel: {}", request.getHotelUuid());
        return CustomApiResponse.ok(selfAssessmentService.startSelfAssessment(request));
    }

    /**
     * Save draft responses (partial completion allowed)
     * PUT /api/v1/self-assessments/{uuid}/draft
     */
    @PutMapping("/{uuid}/draft")
    public CustomApiResponse saveDraft(
            @PathVariable UUID uuid,
            @RequestBody List<FieldResponseDto> responses) {
        log.info("REST: Saving draft for self-assessment: {}", uuid);
        return CustomApiResponse.ok(selfAssessmentService.saveDraft(uuid, responses));
    }

    /**
     * Submit completed self-assessment and calculate scores
     * POST /api/v1/self-assessments/{uuid}/submit
     */
    @PostMapping("/{uuid}/submit")
    public CustomApiResponse submitSelfAssessment(@PathVariable UUID uuid) {
        log.info("REST: Submitting self-assessment: {}", uuid);
        return CustomApiResponse.ok(selfAssessmentService.submitSelfAssessment(uuid));
    }

    /**
     * Get all self-assessments for a hotel
     * GET /api/v1/self-assessments/hotel/{hotelUuid}
     */
    @GetMapping("/hotel/{hotelUuid}")
    public CustomApiResponse getHotelSelfAssessments(@PathVariable UUID hotelUuid) {
        log.info("REST: Getting self-assessments for hotel: {}", hotelUuid);
        return CustomApiResponse.ok(selfAssessmentService.getHotelSelfAssessments(hotelUuid));
    }

    /**
     * Get detailed result for a specific self-assessment
     * GET /api/v1/self-assessments/{uuid}
     */
    @GetMapping("/{uuid}")
    public CustomApiResponse getSelfAssessmentResult(@PathVariable UUID uuid) {
        log.info("REST: Getting result for self-assessment: {}", uuid);
        return CustomApiResponse.ok(selfAssessmentService.getSelfAssessmentResult(uuid));
    }

    /**
     * Delete a self-assessment
     * DELETE /api/v1/self-assessments/{uuid}
     */
    @DeleteMapping("/{uuid}")
    public CustomApiResponse deleteSelfAssessment(@PathVariable UUID uuid) {
        log.info("REST: Deleting self-assessment: {}", uuid);
        selfAssessmentService.deleteSelfAssessment(uuid);
        return CustomApiResponse.ok("Self-assessment deleted successfully");
    }

    /**
     * Compare latest self-assessment with official assessment
     * GET /api/v1/self-assessments/hotel/{hotelUuid}/comparison
     */
    @GetMapping("/hotel/{hotelUuid}/comparison")
    public CustomApiResponse compareAssessments(@PathVariable UUID hotelUuid) {
        log.info("REST: Comparing self-assessment with official for hotel: {}", hotelUuid);
        return CustomApiResponse.ok(selfAssessmentService.compareSelfAndOfficialAssessments(hotelUuid));
    }

    /**
     * Recalculate scores for a self-assessment
     * POST /api/v1/self-assessments/{uuid}/recalculate
     */
    @PostMapping("/{uuid}/recalculate")
    public CustomApiResponse recalculateScores(@PathVariable UUID uuid) {
        log.info("REST: Recalculating scores for self-assessment: {}", uuid);
        return CustomApiResponse.ok(selfAssessmentService.recalculateScores(uuid));
    }
}
