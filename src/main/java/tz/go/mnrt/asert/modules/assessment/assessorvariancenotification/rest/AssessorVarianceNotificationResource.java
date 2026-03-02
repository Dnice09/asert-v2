package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.rest;

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
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos.AssessorVarianceNotificationRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.services.AssessorVarianceNotificationService;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-variance-notifications")
@RequiredArgsConstructor
@Slf4j
public class AssessorVarianceNotificationResource {

  final AssessorVarianceNotificationService assessorVarianceNotificationService;

  @GetMapping
  public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
    log.info("Retrieving assessor-variance-notifications with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
        assessorVarianceNotificationService.findAll(
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
        @Valid @RequestBody AssessorVarianceNotificationRequestDto assessorVarianceNotificationRequestDto,
        BindingResult bindingResult) {
    log.info("Creating new AssessorVarianceNotification: {}", assessorVarianceNotificationRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (assessorVarianceNotificationRequestDto.getId() != null || assessorVarianceNotificationRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (assessorVarianceNotificationRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new AssessorVarianceNotification",
                assessorVarianceNotificationRequestDto.getId().toString(), "assessorVarianceNotificationRequestDto"));
        }
        if (assessorVarianceNotificationRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new AssessorVarianceNotification",
                assessorVarianceNotificationRequestDto.getUuid().toString(), "assessorVarianceNotificationRequestDto"));
        }
        return CustomApiResponse.validationErrors("New AssessorVarianceNotification cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("AssessorVarianceNotification created successfully",
            assessorVarianceNotificationService.save(assessorVarianceNotificationRequestDto));
    } catch (Exception e) {
        log.error("Error creating AssessorVarianceNotification", e);
        return CustomApiResponse.badRequest("Failed to create AssessorVarianceNotification", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.ACCEPTED)
  public CustomApiResponse update(
        @Valid @RequestBody AssessorVarianceNotificationRequestDto assessorVarianceNotificationDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {
    log.info("Updating AssessorVarianceNotification with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (assessorVarianceNotificationDto.getUuid() == null || !Objects.equals(assessorVarianceNotificationDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
            "AssessorVarianceNotification UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("AssessorVarianceNotification updated successfully",
            assessorVarianceNotificationService.save(assessorVarianceNotificationDto));
    } catch (Exception e) {
        log.error("Error updating AssessorVarianceNotification with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update AssessorVarianceNotification", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching AssessorVarianceNotification with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
            assessorVarianceNotificationService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching AssessorVarianceNotification with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("AssessorVarianceNotification with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting AssessorVarianceNotification with UUID: {}", uuid);
    try {
        assessorVarianceNotificationService.delete(uuid);
        return CustomApiResponse.noContent("AssessorVarianceNotification deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting AssessorVarianceNotification with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete AssessorVarianceNotification", e.getMessage());
    }
  }

  /**
   * Get unread notifications for a specific assessor
   */
  @GetMapping("/assessor/{assessorId}/unread")
  public CustomApiResponse getUnreadNotifications(@PathVariable Long assessorId) {
    log.info("Fetching unread notifications for assessor: {}", assessorId);
    try {
        return CustomApiResponse.ok(
            assessorVarianceNotificationService.getUnreadNotifications(assessorId));
    } catch (Exception e) {
        log.error("Error fetching unread notifications for assessor: {}", assessorId, e);
        return CustomApiResponse.badRequest("Failed to fetch unread notifications", e.getMessage());
    }
  }

  /**
   * Mark a notification as read
   */
  @PutMapping("/{uuid}/mark-read")
  @Transactional
  public CustomApiResponse markAsRead(@PathVariable UUID uuid) {
    log.info("Marking notification as read: {}", uuid);
    try {
        assessorVarianceNotificationService.markAsRead(uuid);
        return CustomApiResponse.ok("Notification marked as read");
    } catch (Exception e) {
        log.error("Error marking notification as read: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to mark notification as read", e.getMessage());
    }
  }

  /**
   * Mark all notifications as read for an assessor
   */
  @PutMapping("/assessor/{assessorId}/mark-all-read")
  @Transactional
  public CustomApiResponse markAllAsRead(@PathVariable Long assessorId) {
    log.info("Marking all notifications as read for assessor: {}", assessorId);
    try {
        assessorVarianceNotificationService.markAllAsRead(assessorId);
        return CustomApiResponse.ok("All notifications marked as read");
    } catch (Exception e) {
        log.error("Error marking all notifications as read for assessor: {}", assessorId, e);
        return CustomApiResponse.badRequest("Failed to mark all notifications as read", e.getMessage());
    }
  }
}
