package tz.go.mnrt.asert.modules.setup.educationlevel.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.educationlevel.dto.EducationLevelDto;
import tz.go.mnrt.asert.modules.setup.educationlevel.service.EducationLevelService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/education-levels")
@RequiredArgsConstructor
public class EducationLevelResource {

    private final EducationLevelService educationLevelService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            educationLevelService.findAll(
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
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody EducationLevelDto educationLevelDto) {
        if (educationLevelDto.getId() != null || educationLevelDto.getUuid() != null) {
            throw new ValidationException("New EducationLevel cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Education Level created successfully",
            educationLevelService.save(educationLevelDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody EducationLevelDto educationLevelDto,
        @PathVariable UUID uuid) {

        if (educationLevelDto.getUuid() == null || !Objects.equals(educationLevelDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Education Level id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Education Level updated successfully",
            educationLevelService.save(educationLevelDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            educationLevelService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        educationLevelService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Education Level deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

