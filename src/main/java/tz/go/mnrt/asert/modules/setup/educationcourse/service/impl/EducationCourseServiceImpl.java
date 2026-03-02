package tz.go.mnrt.asert.modules.setup.educationcourse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.educationcourse.dto.EducationCourseDto;
import tz.go.mnrt.asert.modules.setup.educationcourse.entity.EducationCourse;
import tz.go.mnrt.asert.modules.setup.educationcourse.repository.EducationCourseRepository;
import tz.go.mnrt.asert.modules.setup.educationcourse.repository.EducationCourseSpecification;
import tz.go.mnrt.asert.modules.setup.educationcourse.service.EducationCourseService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationCourseServiceImpl implements EducationCourseService {
    private final EducationCourseRepository educationCourseRepository;

    @Override
    public EducationCourseDto save(EducationCourseDto dto) {
        // Check for duplicate name
        educationCourseRepository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            if (dto.getUuid() == null || !existing.getUuid().equals(dto.getUuid())) {
                throw new ValidationException("Education Course with the same name already exists.");
            }
        });
        EducationCourse entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = educationCourseRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Institute not found."));
        } else {
            // New mode
            entity = new EducationCourse();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }

        entity.setName(dto.getName());
        entity.setEducationLevelId(dto.getEducationLevelId());
        entity.setUpdatedAt(LocalDateTime.now());

        educationCourseRepository.save(entity);

        EducationCourseDto response = new EducationCourseDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<EducationCourseDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        Long educationLevelId = null;
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
            String levelIdStr = search.get("educationLevelId");
            if(levelIdStr != null && !levelIdStr.isEmpty()){
                educationLevelId = Long.valueOf(levelIdStr);
            }
        }

        Specification<EducationCourse> spec = Specification.where(EducationCourseSpecification.notDeleted());

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(EducationCourseSpecification.search(filter));
        }

        if (educationLevelId != null && educationLevelId > 0) {
            spec = spec.and(EducationCourseSpecification.byEducationLevel(educationLevelId));
        }

        Page<EducationCourse> results = educationCourseRepository.findAll(spec, page);

        return results.map(EducationCourseDto::new);
    }


    @Override
    public EducationCourseDto findByUuid(UUID uuid) {
        EducationCourse entity = educationCourseRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Institute not found."));
        EducationCourseDto dto = new EducationCourseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        EducationCourse entity = educationCourseRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Institute not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        educationCourseRepository.save(entity);
    }
}

