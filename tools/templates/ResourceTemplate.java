package tz.go.mnrt.asert.modules.#package_name.rest;

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
import tz.go.mnrt.asert.modules.#package_name.dtos.#module_nameRequestDto;
import tz.go.mnrt.asert.modules.#package_name.services.#module_nameService;

@RestController
@RequestMapping(Constant.API_V1 + "/#module_route_name")
@RequiredArgsConstructor
@Slf4j
public class #module_nameResource {

  final #module_nameService #module_varService;

  @GetMapping
  public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
    log.info("Retrieving #module_route_name with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
        #module_varService.findAll(
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
        @Valid @RequestBody #module_nameRequestDto #module_varRequestDto,
        BindingResult bindingResult) {
    log.info("Creating new #module_name: {}", #module_varRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (#module_varRequestDto.getId() != null || #module_varRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (#module_varRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new #module_name",
                #module_varRequestDto.getId().toString(), "#module_varRequestDto"));
        }
        if (#module_varRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new #module_name",
                #module_varRequestDto.getUuid().toString(), "#module_varRequestDto"));
        }
        return CustomApiResponse.validationErrors("New #module_name cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("#module_name created successfully",
            #module_varService.save(#module_varRequestDto));
    } catch (Exception e) {
        log.error("Error creating #module_name", e);
        return CustomApiResponse.badRequest("Failed to create #module_name", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.ACCEPTED)
  public CustomApiResponse update(
        @Valid @RequestBody #module_nameRequestDto #module_varDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {
    log.info("Updating #module_name with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (#module_varDto.getUuid() == null || !Objects.equals(#module_varDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
            "#module_name UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("#module_name updated successfully",
            #module_varService.save(#module_varDto));
    } catch (Exception e) {
        log.error("Error updating #module_name with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update #module_name", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching #module_name with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
            #module_varService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching #module_name with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("#module_name with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting #module_name with UUID: {}", uuid);
    try {
        #module_varService.delete(uuid);
        return CustomApiResponse.noContent("#module_name deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting #module_name with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete #module_name", e.getMessage());
    }
  }
}
