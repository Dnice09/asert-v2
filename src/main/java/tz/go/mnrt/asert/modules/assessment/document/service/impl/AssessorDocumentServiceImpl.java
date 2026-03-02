package tz.go.mnrt.asert.modules.assessment.document.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;
import tz.go.mnrt.asert.modules.assessment.document.entity.AssessorDocument;
import tz.go.mnrt.asert.modules.assessment.document.repository.AssessorDocumentRepository;
import tz.go.mnrt.asert.modules.assessment.document.repository.AssessorDocumentSpecification;
import tz.go.mnrt.asert.modules.assessment.document.service.AssessorDocumentService;

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
public class AssessorDocumentServiceImpl implements AssessorDocumentService {
    private final AssessorDocumentRepository assessorDocumentRepository;
    private final AssessorService assessorService;

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    @Override
    public AssessorDocumentDto save(AssessorDocumentDto dto) {
        AssessorDocument entity;
        if (dto.getUuid() != null) {
            entity = assessorDocumentRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Reference not found."));
        } else {
            // New mode
            entity = new AssessorDocument();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setFileType("pdf");
        entity.setTitle(dto.getTitle());
        entity.setFilePath(dto.getFilePath() != null ? uploadFile(dto.getFilePath()) : null);
        entity.setDocumentTypeId(dto.getDocumentTypeId());
        entity.setVerified(false);
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUploadedAt(LocalDateTime.now());

        assessorDocumentRepository.save(entity);

        AssessorDocumentDto response = new AssessorDocumentDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<AssessorDocumentDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<AssessorDocument> spec = Specification.where(AssessorDocumentSpecification.byAssessor(assessor.getId()).and(AssessorDocumentSpecification.notDeleted()));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorDocumentSpecification.search(filter));
        }

        Page<AssessorDocument> results = assessorDocumentRepository.findAll(spec, page);

        return results.map(r -> new AssessorDocumentDto(r, uploadedFilesUrl));
    }

    @Override
    public List<AssessorDocumentDto> findAll(Long assessorId) {
        Specification<AssessorDocument> spec = Specification.where(AssessorDocumentSpecification.byAssessor(assessorId).and(AssessorDocumentSpecification.notDeleted()));
        return assessorDocumentRepository.findAll(spec).stream().map(r -> new AssessorDocumentDto(r, uploadedFilesUrl)).collect(Collectors.toList());
    }

    @Override
    public AssessorDocumentDto findByUuid(UUID uuid) {
        AssessorDocument entity = assessorDocumentRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Reference not found."));
        AssessorDocumentDto dto = new AssessorDocumentDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        AssessorDocument entity = assessorDocumentRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Reference not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        assessorDocumentRepository.save(entity);
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

