package tz.go.mnrt.asert.modules.setup.institute.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.institute.dto.InstituteDto;
import tz.go.mnrt.asert.modules.setup.institute.service.InstituteService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/education-institutions")
@RequiredArgsConstructor
public class InstituteResource {

    private final InstituteService instituteService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            instituteService.findAll(
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
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody InstituteDto instituteDto) {
        if (instituteDto.getId() != null || instituteDto.getUuid() != null) {
            throw new ValidationException("New EducationLevel cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Institute created successfully",
            instituteService.save(instituteDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody InstituteDto instituteDto,
        @PathVariable UUID uuid) {

        if (instituteDto.getUuid() == null || !Objects.equals(instituteDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Institute id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Institute updated successfully",
            instituteService.save(instituteDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            instituteService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        instituteService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Institute deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

