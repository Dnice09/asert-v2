package tz.go.mnrt.asert.modules.setup.country.rest;

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
import tz.go.mnrt.asert.modules.setup.country.dtos.CountryRequestDto;
import tz.go.mnrt.asert.modules.setup.country.services.CountryService;

@RestController
@RequestMapping(Constant.API_V1 + "/countries")
@RequiredArgsConstructor
@Slf4j
public class CountryResource {

  final CountryService countryService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving countries with pagination: {} and search criteria: {}", pagination, search);
    return  CustomApiResponse.ok(
            countryService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody CountryRequestDto countryRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new Country: {}", countryRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (countryRequestDto.getId() != null || countryRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (countryRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new Country",
                countryRequestDto.getId().toString(), "countryRequestDto"));
        }
        if (countryRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new Country",
                countryRequestDto.getUuid().toString(), "countryRequestDto"));
        }
        return CustomApiResponse.validationErrors("New Country cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("Country created successfully",
                countryService.save(countryRequestDto));
    } catch (Exception e) {
        log.error("Error creating Country", e);
        return CustomApiResponse.badRequest("Failed to create Country", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody CountryRequestDto countryDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating Country with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (countryDto.getUuid() == null || !Objects.equals(countryDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "Country UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("Country updated successfully",
                countryService.save(countryDto));
    } catch (Exception e) {
        log.error("Error updating Country with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update Country", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching Country with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
                countryService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching Country with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("Country with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting Country with UUID: {}", uuid);
    try {
        countryService.delete(uuid);
        return 
            CustomApiResponse.noContent("Country deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting Country with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete Country", e.getMessage());
    }
  }
}
