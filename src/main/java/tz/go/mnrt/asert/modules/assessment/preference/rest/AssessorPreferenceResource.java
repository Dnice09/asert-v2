package tz.go.mnrt.asert.modules.assessment.preference.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.preference.dto.AssessorPreferenceDto;
import tz.go.mnrt.asert.modules.assessment.preference.service.AssessorPreferenceService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-preferences")
@RequiredArgsConstructor
public class AssessorPreferenceResource {

    private final AssessorPreferenceService assessorPreferenceService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            assessorPreferenceService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("preference").ascending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorPreferenceDto assessorPreferenceDto) {
        if (assessorPreferenceDto.getId() != null || assessorPreferenceDto.getUuid() != null) {
            throw new ValidationException("New Preference cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Preference created successfully",
            assessorPreferenceService.save(assessorPreferenceDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody AssessorPreferenceDto assessorPreferenceDto,
        @PathVariable UUID uuid) {

        if (assessorPreferenceDto.getUuid() == null || !Objects.equals(assessorPreferenceDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Preference id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Preference updated successfully",
            assessorPreferenceService.save(assessorPreferenceDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            assessorPreferenceService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorPreferenceService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Preference deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

