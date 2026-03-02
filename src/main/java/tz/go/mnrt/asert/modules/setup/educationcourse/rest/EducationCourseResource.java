package tz.go.mnrt.asert.modules.setup.educationcourse.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.educationcourse.dto.EducationCourseDto;
import tz.go.mnrt.asert.modules.setup.educationcourse.service.EducationCourseService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/education-courses")
@RequiredArgsConstructor
public class EducationCourseResource {

    private final EducationCourseService educationCourseService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            educationCourseService.findAll(
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
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody EducationCourseDto educationCourseDto) {
        if (educationCourseDto.getId() != null || educationCourseDto.getUuid() != null) {
            throw new ValidationException("New Course cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Course created successfully",
            educationCourseService.save(educationCourseDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody EducationCourseDto educationCourseDto,
        @PathVariable UUID uuid) {

        if (educationCourseDto.getUuid() == null || !Objects.equals(educationCourseDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Course id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Course updated successfully",
            educationCourseService.save(educationCourseDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            educationCourseService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        educationCourseService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Course deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

