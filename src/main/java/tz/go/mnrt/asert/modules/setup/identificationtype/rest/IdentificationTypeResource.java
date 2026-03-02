package tz.go.mnrt.asert.modules.setup.identificationtype.rest;

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
import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.identificationtype.services.IdentificationTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/identification-types")
@RequiredArgsConstructor
@Slf4j
public class IdentificationTypeResource {

  final IdentificationTypeService identificationTypeService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving identification-types with pagination: {} and search criteria: {}", pagination, search);
    return 
        CustomApiResponse.ok(
            identificationTypeService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody IdentificationTypeRequestDto identificationTypeRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new IdentificationType: {}", identificationTypeRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (identificationTypeRequestDto.getId() != null || identificationTypeRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (identificationTypeRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new IdentificationType",
                identificationTypeRequestDto.getId().toString(), "identificationTypeRequestDto"));
        }
        if (identificationTypeRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new IdentificationType",
                identificationTypeRequestDto.getUuid().toString(), "identificationTypeRequestDto"));
        }
        return CustomApiResponse.validationErrors("New IdentificationType cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("IdentificationType created successfully",
                identificationTypeService.save(identificationTypeRequestDto));
    } catch (Exception e) {
        log.error("Error creating IdentificationType", e);
        return CustomApiResponse.badRequest("Failed to create IdentificationType", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody IdentificationTypeRequestDto identificationTypeDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating IdentificationType with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (identificationTypeDto.getUuid() == null || !Objects.equals(identificationTypeDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "IdentificationType UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("IdentificationType updated successfully",
                identificationTypeService.save(identificationTypeDto));
    } catch (Exception e) {
        log.error("Error updating IdentificationType with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update IdentificationType", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching IdentificationType with UUID: {}", uuid);
    try {
        return 
            CustomApiResponse.ok(
                identificationTypeService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching IdentificationType with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("IdentificationType with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting IdentificationType with UUID: {}", uuid);
    try {
        identificationTypeService.delete(uuid);
        return CustomApiResponse.noContent("IdentificationType deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting IdentificationType with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete IdentificationType", e.getMessage());
    }
  }
}
