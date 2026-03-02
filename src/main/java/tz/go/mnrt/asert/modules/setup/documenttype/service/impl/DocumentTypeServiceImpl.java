package tz.go.mnrt.asert.modules.setup.documenttype.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.documenttype.dto.DocumentDto;
import tz.go.mnrt.asert.modules.setup.documenttype.entity.DocumentType;
import tz.go.mnrt.asert.modules.setup.documenttype.repository.DocumentTypeRepository;
import tz.go.mnrt.asert.modules.setup.documenttype.repository.DocumentTypeSpecification;
import tz.go.mnrt.asert.modules.setup.documenttype.service.DocumentTypeService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl implements DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;

    @Override
    public DocumentDto save(DocumentDto dto) {
        // Check for duplicate name
        documentTypeRepository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            if (dto.getUuid() == null || !existing.getUuid().equals(dto.getUuid())) {
                throw new ValidationException("Document Type with the same name already exists.");
            }
        });

        DocumentType entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = documentTypeRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Document Type not found."));
        } else {
            // New mode
            entity = new DocumentType();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }

        entity.setName(dto.getName());
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy

        documentTypeRepository.save(entity);

        DocumentDto response = new DocumentDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<DocumentDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }

        Specification<DocumentType> spec = Specification.where(DocumentTypeSpecification.notDeleted());

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(DocumentTypeSpecification.search(filter));
        }

        Page<DocumentType> results = documentTypeRepository.findAll(spec, page);

        return results.map(entity -> {
            DocumentDto dto = new DocumentDto();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }


    @Override
    public DocumentDto findByUuid(UUID uuid) {
        DocumentType entity = documentTypeRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Document Type not found."));
        DocumentDto dto = new DocumentDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        DocumentType entity = documentTypeRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Document Type not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy
        documentTypeRepository.save(entity);
    }
}

