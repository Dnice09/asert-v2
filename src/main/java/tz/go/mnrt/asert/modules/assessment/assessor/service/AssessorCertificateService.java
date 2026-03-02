package tz.go.mnrt.asert.modules.assessment.assessor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorCertificateDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorCertificate;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorCertificateRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import javax.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessorCertificateService {

    private final AssessorCertificateRepository certificateRepository;
    private final AssessorRepository assessorRepository;

    public Page<AssessorCertificateDto> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public List<AssessorCertificateDto> findByAssessorId(Long assessorId) {
        return certificateRepository.findByAssessorIdAndIsActiveTrue(assessorId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AssessorCertificateDto findByUuid(UUID uuid) {
        AssessorCertificate certificate = certificateRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with UUID: " + uuid));
        return convertToDto(certificate);
    }

    public AssessorCertificateDto save(AssessorCertificateDto dto) {
        AssessorCertificate certificate;
        
        if (dto.getUuid() != null) {
            // Update existing certificate
            certificate = certificateRepository.findByUuid(dto.getUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Certificate not found with UUID: " + dto.getUuid()));
            updateCertificateFromDto(certificate, dto);
        } else {
            // Create new certificate
            certificate = new AssessorCertificate();
            updateCertificateFromDto(certificate, dto);
            
            // Set assessor
            if (dto.getAssessorId() != null) {
                Assessor assessor = assessorRepository.findById(dto.getAssessorId())
                        .orElseThrow(() -> new EntityNotFoundException("Assessor not found with ID: " + dto.getAssessorId()));
                certificate.setAssessor(assessor);
            }
            
            // Set default values
            certificate.setVerificationStatus("PENDING");
            certificate.setIsActive(true);
        }

        AssessorCertificate saved = certificateRepository.save(certificate);
        log.info("Certificate saved with UUID: {}", saved.getUuid());
        return convertToDto(saved);
    }

    public void delete(UUID uuid) {
        AssessorCertificate certificate = certificateRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with UUID: " + uuid));
        
        // Soft delete
        certificate.setIsActive(false);
        certificateRepository.save(certificate);
        log.info("Certificate soft deleted with UUID: {}", uuid);
    }

    public AssessorCertificateDto attachFile(UUID certificateUuid, Long fileUploadId) {
        AssessorCertificate certificate = certificateRepository.findByUuid(certificateUuid)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with UUID: " + certificateUuid));

        certificate.setFileUploadId(fileUploadId);
        AssessorCertificate saved = certificateRepository.save(certificate);
        log.info("File attached to certificate with UUID: {}", certificateUuid);
        return convertToDto(saved);
    }

    public AssessorCertificateDto removeFile(UUID certificateUuid) {
        AssessorCertificate certificate = certificateRepository.findByUuid(certificateUuid)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with UUID: " + certificateUuid));
        
        certificate.setFileUploadId(null);
        AssessorCertificate saved = certificateRepository.save(certificate);
        log.info("File removed from certificate with UUID: {}", certificateUuid);
        return convertToDto(saved);
    }

    public List<AssessorCertificateDto> findExpiredCertificates() {
        return certificateRepository.findExpiredCertificates(LocalDate.now())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AssessorCertificateDto> findCertificatesExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysFromNow = now.plusDays(30);
        return certificateRepository.findCertificatesExpiringSoon(now, thirtyDaysFromNow)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void updateCertificateFromDto(AssessorCertificate certificate, AssessorCertificateDto dto) {
        certificate.setCertificateName(dto.getCertificateName());
        certificate.setIssuingAuthority(dto.getIssuingAuthority());
        certificate.setCertificateNumber(dto.getCertificateNumber());
        certificate.setIssueDate(dto.getIssueDate());
        certificate.setExpiryDate(dto.getExpiryDate());
        certificate.setDescription(dto.getDescription());
        certificate.setCertificateType(dto.getCertificateType());
        
        if (dto.getVerificationStatus() != null) {
            certificate.setVerificationStatus(dto.getVerificationStatus());
        }
        if (dto.getVerificationNotes() != null) {
            certificate.setVerificationNotes(dto.getVerificationNotes());
        }
        if (dto.getIsActive() != null) {
            certificate.setIsActive(dto.getIsActive());
        }
        if (dto.getFileUploadId() != null) {
            certificate.setFileUploadId(dto.getFileUploadId());
        }
    }

    private AssessorCertificateDto convertToDto(AssessorCertificate certificate) {
        AssessorCertificateDto dto = new AssessorCertificateDto();
        dto.setId(certificate.getId());
        dto.setUuid(certificate.getUuid());
        dto.setCertificateName(certificate.getCertificateName());
        dto.setIssuingAuthority(certificate.getIssuingAuthority());
        dto.setCertificateNumber(certificate.getCertificateNumber());
        dto.setIssueDate(certificate.getIssueDate());
        dto.setExpiryDate(certificate.getExpiryDate());
        dto.setDescription(certificate.getDescription());
        dto.setCertificateType(certificate.getCertificateType());
        dto.setVerificationStatus(certificate.getVerificationStatus());
        dto.setVerificationNotes(certificate.getVerificationNotes());
        dto.setIsActive(certificate.getIsActive());
        
        if (certificate.getAssessor() != null) {
            dto.setAssessorId(certificate.getAssessor().getId());
            dto.setAssessorUuid(certificate.getAssessor().getUuid());
        }

        // Set file upload fields
        dto.setFileUploadId(certificate.getFileUploadId());
        if (certificate.getFileUpload() != null) {
            dto.setFileName(certificate.getFileUpload().getName());
            dto.setFileType(certificate.getFileUpload().getFileType());
            dto.setFileSize(certificate.getFileUpload().getFileSize());
        }

        // Set computed fields
        dto.setIsExpired(certificate.isExpired());
        dto.setIsExpiringSoon(certificate.isExpiringSoon());
        dto.setHasFile(certificate.hasFile());
        dto.setStatusDisplayName(certificate.getStatusDisplayName());

        return dto;
    }

}