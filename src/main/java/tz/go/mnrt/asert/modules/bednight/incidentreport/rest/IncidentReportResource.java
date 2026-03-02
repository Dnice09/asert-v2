package tz.go.mnrt.asert.modules.bednight.incidentreport.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import tz.go.mnrt.asert.modules.bednight.incidentreport.dtos.IncidentReportRequestDto;
import tz.go.mnrt.asert.modules.bednight.incidentreport.services.IncidentReportService;

@RestController
@RequestMapping(Constant.API_V1 + "/incident-reports")
@RequiredArgsConstructor
@Slf4j
public class IncidentReportResource {

  final IncidentReportService incidentReportService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving incident-reports with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
            incidentReportService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody IncidentReportRequestDto incidentReportRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new IncidentReport: {}", incidentReportRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (incidentReportRequestDto.getId() != null || incidentReportRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (incidentReportRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new IncidentReport",
                incidentReportRequestDto.getId().toString(), "incidentReportRequestDto"));
        }
        if (incidentReportRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new IncidentReport",
                incidentReportRequestDto.getUuid().toString(), "incidentReportRequestDto"));
        }
        return CustomApiResponse.validationErrors("New IncidentReport cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("IncidentReport created successfully",
                incidentReportService.save(incidentReportRequestDto));
    } catch (Exception e) {
        log.error("Error creating IncidentReport", e);
        return CustomApiResponse.badRequest("Failed to create IncidentReport", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody IncidentReportRequestDto incidentReportDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating IncidentReport with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (incidentReportDto.getUuid() == null || !Objects.equals(incidentReportDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "IncidentReport UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("IncidentReport updated successfully",
                incidentReportService.save(incidentReportDto));
    } catch (Exception e) {
        log.error("Error updating IncidentReport with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update IncidentReport", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching IncidentReport with UUID: {}", uuid);

     try {
        return CustomApiResponse.ok(
                incidentReportService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching IncidentReport with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("IncidentReport with UUID " + uuid + " not found");
    }
  }

  @GetMapping("/visitor/{uuid}")
  public CustomApiResponse findByVisitorId(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching IncidentReport with Visitor UUID: {}", uuid);

    try {
        return CustomApiResponse.ok(
                incidentReportService.findByVisitorUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching IncidentReport with Visitor UUID: {}", uuid, e);
        return CustomApiResponse.notFound("IncidentReport with Visitor UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting IncidentReport with UUID: {}", uuid);
    try {
        incidentReportService.delete(uuid);
        return CustomApiResponse.noContent("IncidentReport deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting IncidentReport with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete IncidentReport", e.getMessage());
    }
  }
}
