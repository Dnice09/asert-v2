package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.rest;

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
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.AssessmentVarianceLogRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.HotelVarianceGroupPageResponse;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.services.AssessmentVarianceLogService;

@RestController
@RequestMapping(Constant.API_V1 + "/assessment-variance-logs")
@RequiredArgsConstructor
@Slf4j
public class AssessmentVarianceLogResource {

    final AssessmentVarianceLogService assessmentVarianceLogService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving assessment-variance-logs with pagination: {} and search criteria: {}", pagination, search);
        return CustomApiResponse.ok(
                assessmentVarianceLogService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    /**
     * Get variance logs grouped by hotel with hotel-level pagination.
     * This endpoint groups all variances by hotel first, then paginates the hotel groups.
     * Each page contains N hotels with ALL their variances (maintains parent→child hierarchy).
     *
     * @param pagination Pageable where size = number of hotels per page
     * @param search Filter criteria (status, formId, etc.)
     * @return Paginated response of hotel groups with their variances
     */
    @GetMapping("/grouped-by-hotel")
    public CustomApiResponse getGroupedByHotel(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving variance logs grouped by hotel with pagination: {} and search: {}", pagination, search);
        HotelVarianceGroupPageResponse response = assessmentVarianceLogService.findAllGroupedByHotel(
                PageRequest.of(
                        pagination.getPageNumber(),
                        pagination.getPageSize(),
                        pagination.getSortOr(Sort.by("id").descending())),
                search);
        return CustomApiResponse.ok(response);
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CustomApiResponse create(
            @Valid @RequestBody AssessmentVarianceLogRequestDto assessmentVarianceLogRequestDto,
            BindingResult bindingResult) {
        log.info("Creating new AssessmentVarianceLog: {}", assessmentVarianceLogRequestDto);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate business rules
        if (assessmentVarianceLogRequestDto.getId() != null || assessmentVarianceLogRequestDto.getUuid() != null) {
            List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
            if (assessmentVarianceLogRequestDto.getId() != null) {
                errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new AssessmentVarianceLog",
                        assessmentVarianceLogRequestDto.getId().toString(), "assessmentVarianceLogRequestDto"));
            }
            if (assessmentVarianceLogRequestDto.getUuid() != null) {
                errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new AssessmentVarianceLog",
                        assessmentVarianceLogRequestDto.getUuid().toString(), "assessmentVarianceLogRequestDto"));
            }
            return CustomApiResponse.validationErrors("New AssessmentVarianceLog cannot contain id or uuid", errors);
        }

        try {
            return CustomApiResponse.created("AssessmentVarianceLog created successfully",
                    assessmentVarianceLogService.save(assessmentVarianceLogRequestDto));
        } catch (Exception e) {
            log.error("Error creating AssessmentVarianceLog", e);
            return CustomApiResponse.badRequest("Failed to create AssessmentVarianceLog", e.getMessage());
        }
    }

    @PutMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomApiResponse update(
            @Valid @RequestBody AssessmentVarianceLogRequestDto assessmentVarianceLogDto,
            BindingResult bindingResult,
            @PathVariable UUID uuid) {
        log.info("Updating AssessmentVarianceLog with UUID: {}", uuid);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate UUID consistency
        if (assessmentVarianceLogDto.getUuid() == null || !Objects.equals(assessmentVarianceLogDto.getUuid(), uuid)) {
            return CustomApiResponse.validationError("uuid",
                    "AssessmentVarianceLog UUID must be present and equal to path UUID", uuid.toString());
        }

        try {
            return CustomApiResponse.accepted("AssessmentVarianceLog updated successfully",
                    assessmentVarianceLogService.save(assessmentVarianceLogDto));
        } catch (Exception e) {
            log.error("Error updating AssessmentVarianceLog with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to update AssessmentVarianceLog", e.getMessage());
        }
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching AssessmentVarianceLog with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(
                    assessmentVarianceLogService.findByUuid(uuid));
        } catch (Exception e) {
            log.error("Error fetching AssessmentVarianceLog with UUID: {}", uuid, e);
            return CustomApiResponse.notFound("AssessmentVarianceLog with UUID " + uuid + " not found");
        }
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        log.info("Deleting AssessmentVarianceLog with UUID: {}", uuid);
        try {
            assessmentVarianceLogService.delete(uuid);
            return CustomApiResponse.noContent("AssessmentVarianceLog deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting AssessmentVarianceLog with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to delete AssessmentVarianceLog", e.getMessage());
        }
    }

    /**
     * Get unresolved variances for a hotel and form
     */
    @GetMapping("/unresolved")
    public CustomApiResponse getUnresolvedVariances(
            @RequestParam Long hotelId,
            @RequestParam Long formId) {
        log.info("Fetching unresolved variances for hotel={}, form={}", hotelId, formId);
        try {
            return CustomApiResponse.ok(
                    assessmentVarianceLogService.getUnresolvedVariances(hotelId, formId));
        } catch (Exception e) {
            log.error("Error fetching unresolved variances for hotel={}, form={}", hotelId, formId, e);
            return CustomApiResponse.badRequest("Failed to fetch unresolved variances", e.getMessage());
        }
    }

    /**
     * Mark a variance as resolved
     */
    @PutMapping("/{uuid}/resolve")
    @Transactional
    public CustomApiResponse resolveVariance(@PathVariable UUID uuid) {
        log.info("Resolving variance {}", uuid);
        try {
            assessmentVarianceLogService.resolveVariance(uuid);
            return CustomApiResponse.ok("Variance resolved successfully");
        } catch (Exception e) {
            log.error("Error resolving variance {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to resolve variance", e.getMessage());
        }
    }
}
