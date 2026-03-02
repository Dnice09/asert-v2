package tz.go.mnrt.asert.modules.assessment.certification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;
import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;
import tz.go.mnrt.asert.modules.assessment.certification.repository.AssessorCertificationRepository;
import tz.go.mnrt.asert.modules.assessment.certification.repository.AssessorCertificationSpecification;
import tz.go.mnrt.asert.modules.assessment.certification.service.AssessorCertificationService;

import javax.validation.ValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessorCertificationServiceImpl implements AssessorCertificationService {
    private final AssessorCertificationRepository assessorCertificationRepository;
    private final AssessorService assessorService;

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    @Override
    public AssessorCertificationDto save(AssessorCertificationDto dto) {
        AssessorCertification entity;
        if (dto.getUuid() != null) {
            entity = assessorCertificationRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Certification not found."));
        } else {
            // New mode
            entity = new AssessorCertification();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setTitle(dto.getTitle());
        entity.setIssuer(dto.getIssuer());
        entity.setIssueDate(dto.getIssueDate());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setDescription(dto.getDescription());
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());

        assessorCertificationRepository.save(entity);

        AssessorCertificationDto response = new AssessorCertificationDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<AssessorCertificationDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<AssessorCertification> spec = Specification.where(AssessorCertificationSpecification.byAssessor(assessor.getId()).and(AssessorCertificationSpecification.notDeleted()));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorCertificationSpecification.search(filter));
        }

        Page<AssessorCertification> results = assessorCertificationRepository.findAll(spec, page);

        return results.map(AssessorCertificationDto::new);
    }

    @Override
    public List<AssessorCertificationDto> findAll(Long assessorId) {
        Specification<AssessorCertification> spec = Specification.where(AssessorCertificationSpecification.byAssessor(assessorId).and(AssessorCertificationSpecification.notDeleted()));
        return assessorCertificationRepository.findAll(spec).stream().map(AssessorCertificationDto::new).collect(Collectors.toList());
    }

    @Override
    public AssessorCertificationDto findByUuid(UUID uuid) {
        AssessorCertification entity = assessorCertificationRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Certification not found."));
        AssessorCertificationDto dto = new AssessorCertificationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        AssessorCertification entity = assessorCertificationRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Certification not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        assessorCertificationRepository.save(entity);
    }

    public String uploadFile(String base64File) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64File);

            String fileName = UUID.randomUUID().toString().replace("-", "") + ".pdf";
            Path uploadPath = Paths.get(uploadedFilesUrl);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, decodedBytes);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid base64 file data");
        }
    }
}

