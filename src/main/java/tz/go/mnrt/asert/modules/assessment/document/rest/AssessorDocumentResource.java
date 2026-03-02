package tz.go.mnrt.asert.modules.assessment.document.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;
import tz.go.mnrt.asert.modules.assessment.document.service.AssessorDocumentService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-documents")
@RequiredArgsConstructor
public class AssessorDocumentResource {

    private final AssessorDocumentService assessorDocumentService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            assessorDocumentService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("uploadedAt").ascending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorDocumentDto assessorDocumentDto) {
        if (assessorDocumentDto.getId() != null || assessorDocumentDto.getUuid() != null) {
            throw new ValidationException("New Document cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Document created successfully",
            assessorDocumentService.save(assessorDocumentDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody AssessorDocumentDto assessorDocumentDto,
        @PathVariable UUID uuid) {

        if (assessorDocumentDto.getUuid() == null || !Objects.equals(assessorDocumentDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Document id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Document updated successfully",
            assessorDocumentService.save(assessorDocumentDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            assessorDocumentService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorDocumentService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.accepted(
            "Document deleted successfully",
            uuid
        );
        return ResponseEntity.accepted().body(response);
    }
}

