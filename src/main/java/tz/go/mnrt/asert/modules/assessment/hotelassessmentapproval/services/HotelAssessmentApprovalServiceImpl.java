package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.repository.AssessmentVarianceLogRepository;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalRequestDto;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalResponseDto;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.entity.HotelAssessmentApproval;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.repository.HotelAssessmentApprovalRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmission.repository.FormSubmissionRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelAssessmentApprovalServiceImpl extends SimpleSearchService<HotelAssessmentApproval>
        implements HotelAssessmentApprovalService {
    private final HotelAssessmentApprovalRepository hotelAssessmentApprovalRepository;
    private final FormSubmissionRepository formSubmissionRepository;
    private final AssessmentVarianceLogRepository assessmentVarianceLogRepository;
    private final HotelRepository hotelRepository;
    private final FormRepository formRepository;
    private final UserRepository userRepository;

    @Override
    public HotelAssessmentApprovalRequestDto save(HotelAssessmentApprovalRequestDto hotelAssessmentApprovalRequestDto) {
        HotelAssessmentApproval hotelAssessmentApproval = new HotelAssessmentApproval();
        if (hotelAssessmentApprovalRequestDto.getUuid() != null) {
            hotelAssessmentApproval = hotelAssessmentApprovalRepository
                    .findByUuid(hotelAssessmentApprovalRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "HotelAssessmentApproval with uuid {" + hotelAssessmentApprovalRequestDto.getUuid()
                                            + "} not found"));
        }
        BeanUtils.copyProperties(hotelAssessmentApprovalRequestDto, hotelAssessmentApproval, "uuid");
        assert (hotelAssessmentApproval.getUuid() != null);
        hotelAssessmentApproval = hotelAssessmentApprovalRepository.save(hotelAssessmentApproval);
        hotelAssessmentApprovalRequestDto.setId(hotelAssessmentApproval.getId());
        return hotelAssessmentApprovalRequestDto;
    }

    @Override
    public Page<HotelAssessmentApprovalResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated HotelAssessmentApprovals with page {} and search {} ", page, search);
        return hotelAssessmentApprovalRepository
                .findAll(createSpecification(HotelAssessmentApproval.class, search), page)
                .map(HotelAssessmentApprovalResponseDto::new);
    }

    @Override
    public HotelAssessmentApprovalResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return hotelAssessmentApprovalRepository
                .findByUuid(uuid)
                .map(HotelAssessmentApprovalResponseDto::new)
                .orElseThrow(
                        () -> new ValidationException("HotelAssessmentApproval with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting HotelAssessmentApproval with uuid {} ", uuid);
        hotelAssessmentApprovalRepository.softDelete(uuid);
    }

    @Override
    @Transactional
    public HotelAssessmentApprovalResponseDto createOrUpdateApproval(Long hotelId, Long formId) {
        log.info("Creating/updating approval for hotel={}, form={}", hotelId, formId);

        // Fetch all submissions for this hotel/form in one optimized query
        List<FormSubmission> submissions = formSubmissionRepository
                .findByHotelIdAndFormIdWithScores(hotelId, formId);

        if (submissions.size() < 3) {
            throw new ValidationException("All 3 assessors must submit before creating approval record");
        }

        if (submissions.size() > 3) {
            log.warn("Found {} submissions for hotel={}, form={}. Expected 3.", submissions.size(), hotelId, formId);
        }

        // Calculate final scores as average of all submissions
        double avgTotalScore = submissions.stream()
                .mapToDouble(FormSubmission::getTotalScore)
                .average()
                .orElse(0.0);

        double avgMaxScore = submissions.stream()
                .mapToDouble(FormSubmission::getMaxPossibleScore)
                .average()
                .orElse(0.0);

        double finalPercentage = avgMaxScore > 0 ? (avgTotalScore / avgMaxScore) * 100 : 0.0;

        // Determine rating based on percentage
        String finalRating = determinateRating(finalPercentage);

        // Check for unresolved variances
        List<tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog> unresolvedVariances = assessmentVarianceLogRepository
                .findUnresolvedVariances(hotelId, formId);

        boolean hasUnresolvedVariances = !unresolvedVariances.isEmpty();

        // Find or create approval record
        HotelAssessmentApproval approval = hotelAssessmentApprovalRepository
                .findByHotelIdAndFormId(hotelId, formId)
                .orElseGet(() -> {
                    Hotel hotel = hotelRepository.findById(hotelId)
                            .orElseThrow(() -> new ValidationException("Hotel not found: " + hotelId));
                    Form form = formRepository.findById(formId)
                            .orElseThrow(() -> new ValidationException("Form not found: " + formId));

                    return HotelAssessmentApproval.builder()
                            .hotel(hotel)
                            .form(form)
                            .submission1(submissions.get(0))
                            .submission2(submissions.get(1))
                            .submission3(submissions.size() > 2 ? submissions.get(2) : null)
                            .build();
                });

        // Update scores and status
        approval.setFinalTotalScore(avgTotalScore);
        approval.setFinalMaxScore(avgMaxScore);
        approval.setFinalPercentage(finalPercentage);
        approval.setFinalRating(finalRating);
        approval.setHasUnresolvedVariances(hasUnresolvedVariances);
        approval.setVarianceResolutionCount(
                approval.getVarianceResolutionCount() != null ? approval.getVarianceResolutionCount() + 1 : 1);

        if (!hasUnresolvedVariances) {
            approval.setAllVariancesResolvedAt(LocalDateTime.now());
            approval.setStatus("READY_FOR_DT_SUBMISSION");
        } else {
            approval.setStatus("PENDING_VARIANCE_RESOLUTION");
            approval.setAllVariancesResolvedAt(null);
        }

        approval = hotelAssessmentApprovalRepository.save(approval);

        log.info("Approval created/updated: uuid={}, status={}, hasVariances={}",
                approval.getUuid(), approval.getStatus(), hasUnresolvedVariances);

        return new HotelAssessmentApprovalResponseDto(approval);
    }

    @Override
    @Transactional
    public HotelAssessmentApprovalResponseDto submitForDtApproval(UUID approvalUuid) {
        log.info("Submitting approval {} for DT review", approvalUuid);

        HotelAssessmentApproval approval = hotelAssessmentApprovalRepository
                .findByUuid(approvalUuid)
                .orElseThrow(() -> new ValidationException("Approval not found: " + approvalUuid));

        if (Boolean.TRUE.equals(approval.getHasUnresolvedVariances())) {
            throw new ValidationException("Cannot submit to DT: unresolved variances exist");
        }

        if (!"READY_FOR_DT_SUBMISSION".equals(approval.getStatus())) {
            throw new ValidationException(
                    "Approval not ready for DT submission. Current status: " + approval.getStatus());
        }

        approval.setStatus("PENDING_DT_APPROVAL");
        approval.setSubmittedToDtAt(LocalDateTime.now());

        approval = hotelAssessmentApprovalRepository.save(approval);

        log.info("Approval {} submitted to DT at {}", approvalUuid, approval.getSubmittedToDtAt());

        return new HotelAssessmentApprovalResponseDto(approval);
    }

    @Override
    @Transactional
    public HotelAssessmentApprovalResponseDto approveByDt(UUID approvalUuid, Long dtUserId, String comments) {
        log.info("DT approval by user={} for approval={}", dtUserId, approvalUuid);

        HotelAssessmentApproval approval = hotelAssessmentApprovalRepository
                .findByUuid(approvalUuid)
                .orElseThrow(() -> new ValidationException("Approval not found: " + approvalUuid));

        if (!"PENDING_DT_APPROVAL".equals(approval.getStatus())) {
            throw new ValidationException("Approval is not pending DT review. Current status: " + approval.getStatus());
        }

        User dtUser = userRepository.findById(dtUserId)
                .orElseThrow(() -> new ValidationException("DT user not found: " + dtUserId));

        approval.setStatus("DT_APPROVED");
        approval.setApprovedByDt(dtUser);
        approval.setDtApprovalDate(LocalDateTime.now());
        approval.setDtComments(comments);

        approval = hotelAssessmentApprovalRepository.save(approval);

        log.info("Approval {} approved by DT at {}", approvalUuid, approval.getDtApprovalDate());

        return new HotelAssessmentApprovalResponseDto(approval);
    }

    @Override
    @Transactional
    public HotelAssessmentApprovalResponseDto rejectByDt(UUID approvalUuid, Long dtUserId, String rejectionReason) {
        log.info("DT rejection by user={} for approval={}", dtUserId, approvalUuid);

        HotelAssessmentApproval approval = hotelAssessmentApprovalRepository
                .findByUuid(approvalUuid)
                .orElseThrow(() -> new ValidationException("Approval not found: " + approvalUuid));

        if (!"PENDING_DT_APPROVAL".equals(approval.getStatus())) {
            throw new ValidationException("Approval is not pending DT review. Current status: " + approval.getStatus());
        }

        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new ValidationException("Rejection reason is required");
        }

        User dtUser = userRepository.findById(dtUserId)
                .orElseThrow(() -> new ValidationException("DT user not found: " + dtUserId));

        approval.setStatus("DT_REJECTED");
        approval.setApprovedByDt(dtUser);
        approval.setDtRejectionReason(rejectionReason);

        approval = hotelAssessmentApprovalRepository.save(approval);

        log.info("Approval {} rejected by DT. Reason: {}", approvalUuid, rejectionReason);

        return new HotelAssessmentApprovalResponseDto(approval);
    }

    @Override
    public Page<HotelAssessmentApprovalResponseDto> getPendingDtApprovals(Pageable pageable) {
        log.info("Fetching pending DT approvals, page={}", pageable);
        return hotelAssessmentApprovalRepository
                .findPendingDtApprovals(pageable)
                .map(HotelAssessmentApprovalResponseDto::new);
    }

    @Override
    public HotelAssessmentApprovalResponseDto findByHotelAndForm(Long hotelId, Long formId) {
        log.info("Finding approval for hotel={}, form={}", hotelId, formId);
        return hotelAssessmentApprovalRepository
                .findByHotelIdAndFormId(hotelId, formId)
                .map(HotelAssessmentApprovalResponseDto::new)
                .orElse(null);
    }

    /**
     * Determine rating based on percentage score.
     * TODO: This should be configurable
     */
    private String determinateRating(double percentage) {
        if (percentage >= 90)
            return "5 STAR";
        if (percentage >= 80)
            return "4 STAR";
        if (percentage >= 70)
            return "3 STAR";
        if (percentage >= 60)
            return "2 STAR";
        if (percentage >= 50)
            return "1 STAR";
        return "UNRATED";
    }
}
