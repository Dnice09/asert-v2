package tz.go.mnrt.asert.modules.setup.incidentreporttype.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.services.IncidentReportTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/incident-report-types")
@RequiredArgsConstructor
@Slf4j
public class IncidentReportTypeResource {

  final IncidentReportTypeService incidentReportTypeService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving incident-report-types with pagination: {} and search criteria: {}", pagination, search);
    return 
        CustomApiResponse.ok(
            incidentReportTypeService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody IncidentReportTypeRequestDto incidentReportTypeRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new IncidentReportType: {}", incidentReportTypeRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (incidentReportTypeRequestDto.getId() != null || incidentReportTypeRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (incidentReportTypeRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new IncidentReportType",
                incidentReportTypeRequestDto.getId().toString(), "incidentReportTypeRequestDto"));
        }
        if (incidentReportTypeRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new IncidentReportType",
                incidentReportTypeRequestDto.getUuid().toString(), "incidentReportTypeRequestDto"));
        }
        return CustomApiResponse.validationErrors("New IncidentReportType cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("IncidentReportType created successfully",
                incidentReportTypeService.save(incidentReportTypeRequestDto));
    } catch (Exception e) {
        log.error("Error creating IncidentReportType", e);
        return CustomApiResponse.badRequest("Failed to create IncidentReportType", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody IncidentReportTypeRequestDto incidentReportTypeDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating IncidentReportType with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (incidentReportTypeDto.getUuid() == null || !Objects.equals(incidentReportTypeDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "IncidentReportType UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("IncidentReportType updated successfully",
                incidentReportTypeService.save(incidentReportTypeDto));
    } catch (Exception e) {
        log.error("Error updating IncidentReportType with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update IncidentReportType", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching IncidentReportType with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
                incidentReportTypeService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching IncidentReportType with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("IncidentReportType with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting IncidentReportType with UUID: {}", uuid);
    try {
        incidentReportTypeService.delete(uuid);
        return CustomApiResponse.noContent("IncidentReportType deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting IncidentReportType with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete IncidentReportType", e.getMessage());
    }
  }
}
