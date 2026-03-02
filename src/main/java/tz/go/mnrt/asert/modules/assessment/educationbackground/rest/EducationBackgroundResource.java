package tz.go.mnrt.asert.modules.assessment.educationbackground.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;
import tz.go.mnrt.asert.modules.assessment.educationbackground.service.EducationBackgroundService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-education-background")
@RequiredArgsConstructor
public class EducationBackgroundResource {

    private final EducationBackgroundService educationBackgroundService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            educationBackgroundService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("fromDate").descending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody EducationBackgroundDto educationBackgroundDto) {
        if (educationBackgroundDto.getId() != null || educationBackgroundDto.getUuid() != null) {
            throw new ValidationException("New Education Background cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Education Background created successfully",
            educationBackgroundService.save(educationBackgroundDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody EducationBackgroundDto educationBackgroundDto,
        @PathVariable UUID uuid) {

        if (educationBackgroundDto.getUuid() == null || !Objects.equals(educationBackgroundDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Education Background id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Education Background updated successfully",
            educationBackgroundService.save(educationBackgroundDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            educationBackgroundService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        educationBackgroundService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Education Background deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

