package tz.go.mnrt.asert.modules.systemconfiguration.rest;

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
import tz.go.mnrt.asert.modules.systemconfiguration.dtos.SystemConfigurationRequestDto;
import tz.go.mnrt.asert.modules.systemconfiguration.services.SystemConfigurationService;

@RestController
@RequestMapping(Constant.API_V1 + "/system-configurations")
@RequiredArgsConstructor
@Slf4j
public class SystemConfigurationResource {

  final SystemConfigurationService systemConfigurationService;

  @GetMapping
  public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
    log.info("Retrieving system-configurations with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
        systemConfigurationService.findAll(
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
        @Valid @RequestBody SystemConfigurationRequestDto systemConfigurationRequestDto,
        BindingResult bindingResult) {
    log.info("Creating new SystemConfiguration: {}", systemConfigurationRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (systemConfigurationRequestDto.getId() != null || systemConfigurationRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (systemConfigurationRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new SystemConfiguration",
                systemConfigurationRequestDto.getId().toString(), "systemConfigurationRequestDto"));
        }
        if (systemConfigurationRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new SystemConfiguration",
                systemConfigurationRequestDto.getUuid().toString(), "systemConfigurationRequestDto"));
        }
        return CustomApiResponse.validationErrors("New SystemConfiguration cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("SystemConfiguration created successfully",
            systemConfigurationService.save(systemConfigurationRequestDto));
    } catch (Exception e) {
        log.error("Error creating SystemConfiguration", e);
        return CustomApiResponse.badRequest("Failed to create SystemConfiguration", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.ACCEPTED)
  public CustomApiResponse update(
        @Valid @RequestBody SystemConfigurationRequestDto systemConfigurationDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {
    log.info("Updating SystemConfiguration with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (systemConfigurationDto.getUuid() == null || !Objects.equals(systemConfigurationDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
            "SystemConfiguration UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("SystemConfiguration updated successfully",
            systemConfigurationService.save(systemConfigurationDto));
    } catch (Exception e) {
        log.error("Error updating SystemConfiguration with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update SystemConfiguration", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching SystemConfiguration with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
            systemConfigurationService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching SystemConfiguration with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("SystemConfiguration with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting SystemConfiguration with UUID: {}", uuid);
    try {
        systemConfigurationService.delete(uuid);
        return CustomApiResponse.noContent("SystemConfiguration deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting SystemConfiguration with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete SystemConfiguration", e.getMessage());
    }
  }

  /**
   * Get variance threshold configuration
   */
  @GetMapping("/variance-threshold")
  public CustomApiResponse getVarianceThreshold() {
    log.info("Fetching variance threshold configuration");
    try {
        return CustomApiResponse.ok(
            systemConfigurationService.getVarianceThreshold());
    } catch (Exception e) {
        log.error("Error fetching variance threshold", e);
        return CustomApiResponse.badRequest("Failed to fetch variance threshold", e.getMessage());
    }
  }

  /**
   * Update variance threshold configuration
   */
  @PutMapping("/variance-threshold")
  @Transactional
  public CustomApiResponse updateVarianceThreshold(@RequestParam Double threshold) {
    log.info("Updating variance threshold to: {}", threshold);
    try {
        if (threshold == null || threshold < 0) {
            return CustomApiResponse.badRequest("Threshold must be a positive number", null);
        }
        return CustomApiResponse.ok(
            "Variance threshold updated successfully",
            systemConfigurationService.updateVarianceThreshold(threshold));
    } catch (Exception e) {
        log.error("Error updating variance threshold", e);
        return CustomApiResponse.badRequest("Failed to update variance threshold", e.getMessage());
    }
  }

  /**
   * Get a configuration by key
   */
  @GetMapping("/by-key/{configKey}")
  public CustomApiResponse getByKey(@PathVariable String configKey) {
    log.info("Fetching configuration with key: {}", configKey);
    try {
        return CustomApiResponse.ok(
            systemConfigurationService.getByKey(configKey));
    } catch (Exception e) {
        log.error("Error fetching configuration with key: {}", configKey, e);
        return CustomApiResponse.notFound("Configuration with key " + configKey + " not found");
    }
  }

  /**
   * Update a configuration by key
   */
  @PutMapping("/by-key/{configKey}")
  @Transactional
  public CustomApiResponse updateByKey(
      @PathVariable String configKey,
      @RequestParam String value) {
    log.info("Updating configuration {} with value: {}", configKey, value);
    try {
        return CustomApiResponse.ok(
            "Configuration updated successfully",
            systemConfigurationService.updateByKey(configKey, value));
    } catch (Exception e) {
        log.error("Error updating configuration with key: {}", configKey, e);
        return CustomApiResponse.badRequest("Failed to update configuration", e.getMessage());
    }
  }
}
