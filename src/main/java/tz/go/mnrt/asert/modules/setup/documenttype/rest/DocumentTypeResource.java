package tz.go.mnrt.asert.modules.setup.documenttype.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.documenttype.dto.DocumentDto;
import tz.go.mnrt.asert.modules.setup.documenttype.service.DocumentTypeService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/document-types")
@RequiredArgsConstructor
public class DocumentTypeResource {

    private final DocumentTypeService documentTypeService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            documentTypeService.findAll(
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
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody DocumentDto educationLevelDto) {
        if (educationLevelDto.getId() != null || educationLevelDto.getUuid() != null) {
            throw new ValidationException("New Document Type cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Document Type created successfully",
            documentTypeService.save(educationLevelDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody DocumentDto educationLevelDto,
        @PathVariable UUID uuid) {

        if (educationLevelDto.getUuid() == null || !Objects.equals(educationLevelDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Document Type id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Document Type updated successfully",
            documentTypeService.save(educationLevelDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            documentTypeService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        documentTypeService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Document Type deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

