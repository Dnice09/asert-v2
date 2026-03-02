package tz.go.mnrt.asert.modules.bednight.visitor.rest;

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
import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorRequestDto;
import tz.go.mnrt.asert.modules.bednight.visitor.services.VisitorService;

@RestController
@RequestMapping(Constant.API_V1 + "/visitors")
@RequiredArgsConstructor
@Slf4j
public class VisitorResource {

  final VisitorService visitorService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving visitors with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
            visitorService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody VisitorRequestDto visitorRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new Visitor: {}", visitorRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (visitorRequestDto.getId() != null || visitorRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (visitorRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new Visitor",
                visitorRequestDto.getId().toString(), "visitorRequestDto"));
        }
        if (visitorRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new Visitor",
                visitorRequestDto.getUuid().toString(), "visitorRequestDto"));
        }
        return CustomApiResponse.validationErrors("New Visitor cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("Visitor created successfully",
                visitorService.save(visitorRequestDto));
    } catch (Exception e) {
        log.error("Error creating Visitor", e);
        return CustomApiResponse.badRequest("Failed to create Visitor", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody VisitorRequestDto visitorDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating Visitor with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (visitorDto.getUuid() == null || !Objects.equals(visitorDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "Visitor UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("Visitor updated successfully",
                visitorService.save(visitorDto));
    } catch (Exception e) {
        log.error("Error updating Visitor with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update Visitor", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching Visitor with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
                visitorService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching Visitor with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("Visitor with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting Visitor with UUID: {}", uuid);
    try {
        visitorService.delete(uuid);
        return CustomApiResponse.noContent("Visitor deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting Visitor with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete Visitor", e.getMessage());
    }
  }
}
