package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalRequestDto;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.services.HotelAssessmentApprovalService;

@RestController
@RequestMapping(Constant.API_V1 + "/hotel-assessment-approvals")
@RequiredArgsConstructor
@Slf4j
public class HotelAssessmentApprovalResource {

  final HotelAssessmentApprovalService hotelAssessmentApprovalService;

  @GetMapping
  public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
    log.info("Retrieving hotel-assessment-approvals with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
        hotelAssessmentApprovalService.findAll(
            PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                pagination.getSortOr(Sort.by("id").descending())),
            search));
  }

  @PostMapping
  @Transactional
  @ResponseStatus(HttpStatus.CREATED)
  public CustomApiResponse create(
        @Valid @RequestBody HotelAssessmentApprovalRequestDto hotelAssessmentApprovalRequestDto,
        BindingResult bindingResult) {
    log.info("Creating new HotelAssessmentApproval: {}", hotelAssessmentApprovalRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (hotelAssessmentApprovalRequestDto.getId() != null || hotelAssessmentApprovalRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (hotelAssessmentApprovalRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new HotelAssessmentApproval",
                hotelAssessmentApprovalRequestDto.getId().toString(), "hotelAssessmentApprovalRequestDto"));
        }
        if (hotelAssessmentApprovalRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new HotelAssessmentApproval",
                hotelAssessmentApprovalRequestDto.getUuid().toString(), "hotelAssessmentApprovalRequestDto"));
        }
        return CustomApiResponse.validationErrors("New HotelAssessmentApproval cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("HotelAssessmentApproval created successfully",
            hotelAssessmentApprovalService.save(hotelAssessmentApprovalRequestDto));
    } catch (Exception e) {
        log.error("Error creating HotelAssessmentApproval", e);
        return CustomApiResponse.badRequest("Failed to create HotelAssessmentApproval", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.ACCEPTED)
  public CustomApiResponse update(
        @Valid @RequestBody HotelAssessmentApprovalRequestDto hotelAssessmentApprovalDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {
    log.info("Updating HotelAssessmentApproval with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (hotelAssessmentApprovalDto.getUuid() == null || !Objects.equals(hotelAssessmentApprovalDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
            "HotelAssessmentApproval UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("HotelAssessmentApproval updated successfully",
            hotelAssessmentApprovalService.save(hotelAssessmentApprovalDto));
    } catch (Exception e) {
        log.error("Error updating HotelAssessmentApproval with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update HotelAssessmentApproval", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching HotelAssessmentApproval with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
            hotelAssessmentApprovalService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching HotelAssessmentApproval with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("HotelAssessmentApproval with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting HotelAssessmentApproval with UUID: {}", uuid);
    try {
        hotelAssessmentApprovalService.delete(uuid);
        return CustomApiResponse.noContent("HotelAssessmentApproval deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting HotelAssessmentApproval with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete HotelAssessmentApproval", e.getMessage());
    }
  }

  /**
   * Create or update approval record for a hotel and form
   */
  @PostMapping("/create-or-update")
  @Transactional
  public CustomApiResponse createOrUpdateApproval(
      @RequestParam Long hotelId,
      @RequestParam Long formId) {
    log.info("Creating/updating approval for hotel={}, form={}", hotelId, formId);
    try {
        return CustomApiResponse.ok(
            "Approval record created/updated successfully",
            hotelAssessmentApprovalService.createOrUpdateApproval(hotelId, formId));
    } catch (Exception e) {
        log.error("Error creating/updating approval for hotel={}, form={}", hotelId, formId, e);
        return CustomApiResponse.badRequest("Failed to create/update approval", e.getMessage());
    }
  }

  /**
   * Submit assessment for DT approval
   */
  @PutMapping("/{uuid}/submit-for-dt")
  @Transactional
  public CustomApiResponse submitForDtApproval(@PathVariable UUID uuid) {
    log.info("Submitting approval {} for DT review", uuid);
    try {
        return CustomApiResponse.ok(
            "Assessment submitted for DT approval successfully",
            hotelAssessmentApprovalService.submitForDtApproval(uuid));
    } catch (Exception e) {
        log.error("Error submitting approval {} for DT", uuid, e);
        return CustomApiResponse.badRequest("Failed to submit for DT approval", e.getMessage());
    }
  }

  /**
   * DT approves the assessment
   */
  @PutMapping("/{uuid}/dt-approve")
  @Transactional
  public CustomApiResponse approveByDt(
      @PathVariable UUID uuid,
      @RequestParam Long dtUserId,
      @RequestParam(required = false) String comments) {
    log.info("DT approval by user={} for approval={}", dtUserId, uuid);
    try {
        return CustomApiResponse.ok(
            "Assessment approved by DT successfully",
            hotelAssessmentApprovalService.approveByDt(uuid, dtUserId, comments));
    } catch (Exception e) {
        log.error("Error DT approving assessment {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to approve assessment", e.getMessage());
    }
  }

  /**
   * DT rejects the assessment
   */
  @PutMapping("/{uuid}/dt-reject")
  @Transactional
  public CustomApiResponse rejectByDt(
      @PathVariable UUID uuid,
      @RequestParam Long dtUserId,
      @RequestParam String rejectionReason) {
    log.info("DT rejection by user={} for approval={}", dtUserId, uuid);
    try {
        return CustomApiResponse.ok(
            "Assessment rejected by DT",
            hotelAssessmentApprovalService.rejectByDt(uuid, dtUserId, rejectionReason));
    } catch (Exception e) {
        log.error("Error DT rejecting assessment {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to reject assessment", e.getMessage());
    }
  }

  /**
   * Get all pending DT approvals
   */
  @GetMapping("/pending-dt-approvals")
  public CustomApiResponse getPendingDtApprovals(Pageable pagination) {
    log.info("Fetching pending DT approvals with pagination: {}", pagination);
    try {
        return CustomApiResponse.ok(
            hotelAssessmentApprovalService.getPendingDtApprovals(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("submittedToDtAt").descending()))));
    } catch (Exception e) {
        log.error("Error fetching pending DT approvals", e);
        return CustomApiResponse.badRequest("Failed to fetch pending DT approvals", e.getMessage());
    }
  }

  /**
   * Get approval record by hotel and form
   */
  @GetMapping("/by-hotel-form")
  public CustomApiResponse findByHotelAndForm(
      @RequestParam Long hotelId,
      @RequestParam Long formId) {
    log.info("Fetching approval for hotel={}, form={}", hotelId, formId);
    try {
        return CustomApiResponse.ok(
            hotelAssessmentApprovalService.findByHotelAndForm(hotelId, formId));
    } catch (Exception e) {
        log.error("Error fetching approval for hotel={}, form={}", hotelId, formId, e);
        return CustomApiResponse.badRequest("Failed to fetch approval", e.getMessage());
    }
  }
}
