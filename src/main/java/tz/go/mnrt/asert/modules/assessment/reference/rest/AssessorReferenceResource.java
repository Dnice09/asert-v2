package tz.go.mnrt.asert.modules.assessment.reference.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;
import tz.go.mnrt.asert.modules.assessment.reference.service.AssessorReferenceService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-references")
@RequiredArgsConstructor
public class AssessorReferenceResource {

    private final AssessorReferenceService assessorReferenceService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            assessorReferenceService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("name").ascending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorReferenceDto assessorReferenceDto) {
        if (assessorReferenceDto.getId() != null || assessorReferenceDto.getUuid() != null) {
            throw new ValidationException("New Reference cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Reference created successfully",
            assessorReferenceService.save(assessorReferenceDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody AssessorReferenceDto assessorReferenceDto,
        @PathVariable UUID uuid) {

        if (assessorReferenceDto.getUuid() == null || !Objects.equals(assessorReferenceDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Reference id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Reference updated successfully",
            assessorReferenceService.save(assessorReferenceDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            assessorReferenceService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorReferenceService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Reference deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

