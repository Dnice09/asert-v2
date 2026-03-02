package tz.go.mnrt.asert.modules.assessment.educationbackground.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;
import tz.go.mnrt.asert.modules.assessment.educationbackground.entity.EducationBackground;
import tz.go.mnrt.asert.modules.assessment.educationbackground.repository.EducationBackgroundRepository;
import tz.go.mnrt.asert.modules.assessment.educationbackground.repository.EducationBackgroundSpecification;
import tz.go.mnrt.asert.modules.assessment.educationbackground.service.EducationBackgroundService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationBackgroundServiceImpl implements EducationBackgroundService {
    private final EducationBackgroundRepository educationBackgroundRepository;
    private final AssessorService assessorService;

    @Override
    public EducationBackgroundDto save(EducationBackgroundDto dto) {
        EducationBackground entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = educationBackgroundRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Employment History not found."));
        } else {
            // New mode
            entity = new EducationBackground();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setInstitution(dto.getInstitution());
        entity.setCourse(dto.getCourse());
        entity.setFromDate(dto.getFromDate());
        entity.setToDate(dto.getToDate());
        entity.setGraduated(dto.getGraduated());
        entity.setEducationLevelId(dto.getEducationLevelId());
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());

        educationBackgroundRepository.save(entity);

        EducationBackgroundDto response = new EducationBackgroundDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<EducationBackgroundDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<EducationBackground> spec = Specification.where(EducationBackgroundSpecification.byAssessor(assessor.getId()).and(EducationBackgroundSpecification.notDeleted()));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(EducationBackgroundSpecification.search(filter));
        }

        Page<EducationBackground> results = educationBackgroundRepository.findAll(spec, page);

        return results.map(EducationBackgroundDto::new);
    }

    @Override
    public List<EducationBackgroundDto> findAll(Long assessorId) {
        Specification<EducationBackground> spec = Specification.where(EducationBackgroundSpecification.byAssessor(assessorId).and(EducationBackgroundSpecification.notDeleted()));
        return educationBackgroundRepository.findAll(spec).stream().map(EducationBackgroundDto::new).collect(Collectors.toList());
    }

    @Override
    public EducationBackgroundDto findByUuid(UUID uuid) {
        EducationBackground entity = educationBackgroundRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Education History not found."));
        EducationBackgroundDto dto = new EducationBackgroundDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        EducationBackground entity = educationBackgroundRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Education History not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        educationBackgroundRepository.save(entity);
    }
}

