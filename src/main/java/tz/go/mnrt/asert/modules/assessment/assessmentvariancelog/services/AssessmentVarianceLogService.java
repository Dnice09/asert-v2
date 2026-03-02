package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.AssessmentVarianceLogRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.AssessmentVarianceLogResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.HotelVarianceGroupPageResponse;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;

public interface AssessmentVarianceLogService {

  AssessmentVarianceLogRequestDto save(AssessmentVarianceLogRequestDto assessmentVarianceLogDto);

  Page<AssessmentVarianceLogResponseDto> findAll(Pageable page, Map<String, String> search);

  /**
   * Find all variance logs grouped by hotel with hotel-level pagination.
   * This method groups variances by hotel first, then paginates the hotel groups.
   * Each page contains N hotels with ALL their variances (maintaining hierarchy).
   *
   * @param page Pageable object where size = hotels per page
   * @param search Search/filter criteria (e.g., status, formId)
   * @return Paginated response of hotel groups with their variances
   */
  HotelVarianceGroupPageResponse findAllGroupedByHotel(Pageable page, Map<String, String> search);

  AssessmentVarianceLogResponseDto findByUuid(UUID id);

  void delete(UUID uuid);

  /**
   * Detect variances for a newly submitted assessment by comparing section scores
   * with existing submissions for the same hotel and form.
   * Uses pairwise comparison and configurable threshold.
   *
   * Performance optimized with:
   * - Single query to fetch related submissions
   * - Indexed lookups for section scores
   * - Early exit if no other submissions exist
   * - Batch insert for variance logs and notifications
   *
   * @param submission The newly submitted form submission
   * @return List of detected variance logs (empty if no variances)
   */
  List<AssessmentVarianceLogResponseDto> detectVariances(FormSubmission submission);

  /**
   * Get all unresolved variances for a specific hotel and form.
   * Used to check if DT approval can proceed.
   *
   * @param hotelId The hotel ID
   * @param formId The form ID
   * @return List of unresolved variance logs
   */
  List<AssessmentVarianceLogResponseDto> getUnresolvedVariances(Long hotelId, Long formId);

  /**
   * Mark a variance as resolved.
   *
   * @param varianceUuid The variance log UUID
   */
  void resolveVariance(UUID varianceUuid);

  /**
   * Check existing variance logs and update their status to RESOLVED if the
   * variance no longer exists after a variance resolution submission.
   * This method should be called after a variance resolution submission is saved.
   *
   * @param submission The variance resolution submission
   */
  void checkAndResolveVariances(FormSubmission submission);
}

