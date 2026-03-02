package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalRequestDto;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalResponseDto;

public interface HotelAssessmentApprovalService {

  HotelAssessmentApprovalRequestDto save(HotelAssessmentApprovalRequestDto hotelAssessmentApprovalDto);

  Page<HotelAssessmentApprovalResponseDto> findAll(Pageable page, Map<String, String> search);

  HotelAssessmentApprovalResponseDto findByUuid(UUID id);

  void delete(UUID uuid);

  /**
   * Create or update approval record when all 3 assessors have submitted.
   * Calculates final scores as average of 3 submissions.
   * Checks for unresolved variances and sets status accordingly.
   *
   * Performance optimized:
   * - Single query to fetch all 3 submissions with scores
   * - Batch variance check query
   * - Efficient score averaging
   *
   * @param hotelId The hotel ID
   * @param formId The form ID
   * @return The created/updated approval record
   */
  HotelAssessmentApprovalResponseDto createOrUpdateApproval(Long hotelId, Long formId);

  /**
   * Submit assessment to Director of Tourism for approval.
   * Can only be called when all variances are resolved.
   *
   * @param approvalUuid The approval record UUID
   * @return Updated approval record
   */
  HotelAssessmentApprovalResponseDto submitForDtApproval(UUID approvalUuid);

  /**
   * DT approves the assessment.
   *
   * @param approvalUuid The approval record UUID
   * @param dtUserId The DT user ID
   * @param comments Optional DT comments
   * @return Updated approval record
   */
  HotelAssessmentApprovalResponseDto approveByDt(UUID approvalUuid, Long dtUserId, String comments);

  /**
   * DT rejects the assessment.
   *
   * @param approvalUuid The approval record UUID
   * @param dtUserId The DT user ID
   * @param rejectionReason Reason for rejection
   * @return Updated approval record
   */
  HotelAssessmentApprovalResponseDto rejectByDt(UUID approvalUuid, Long dtUserId, String rejectionReason);

  /**
   * Get all pending approvals waiting for DT review.
   *
   * @param pageable Pagination parameters
   * @return Page of pending approvals
   */
  Page<HotelAssessmentApprovalResponseDto> getPendingDtApprovals(Pageable pageable);

  /**
   * Get approval record by hotel and form.
   *
   * @param hotelId The hotel ID
   * @param formId The form ID
   * @return Approval record if exists
   */
  HotelAssessmentApprovalResponseDto findByHotelAndForm(Long hotelId, Long formId);
}

