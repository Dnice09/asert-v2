package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.dto.EmploymentHistoryDto;
import tz.go.mnrt.asert.modules.assessment.preference.dto.AssessorPreferenceDto;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;

@Getter
@Setter
@NoArgsConstructor
public class AssessorProfileDto {

    private AssessorDto assessor;
    private List<EducationBackgroundDto> educationBackgroundList;
    private List<AssessorCertificationDto> certificationDtoList;
    private List<EmploymentHistoryDto> employmentHistoryDtoList;
    private List<AssessorDocumentDto> documentDtoList;
    private List<AssessorReferenceDto> referenceDtoList;
    private List<AssessorPreferenceDto> preferenceDtoList;
    private List<AssessorCertificateDto> certificateList;

    public AssessorProfileDto(Assessor assessor) {
        this.assessor = new AssessorDto(assessor, "");

        // Populate related data from entity relationships
        this.educationBackgroundList = assessor.getEducationBackgrounds() != null
                ? assessor.getEducationBackgrounds().stream()
                        .map(EducationBackgroundDto::new)
                        .collect(Collectors.toList())
                : List.of();

        this.certificationDtoList = assessor.getCertifications() != null ? assessor.getCertifications().stream()
                .map(AssessorCertificationDto::new)
                .collect(Collectors.toList()) : List.of();

        this.employmentHistoryDtoList = assessor.getEmploymentHistories() != null
                ? assessor.getEmploymentHistories().stream()
                        .map(EmploymentHistoryDto::new)
                        .collect(Collectors.toList())
                : List.of();

        this.documentDtoList = assessor.getDocuments() != null ? assessor.getDocuments().stream()
                .map(document -> new AssessorDocumentDto(document, ""))
                .collect(Collectors.toList()) : List.of();

        this.referenceDtoList = assessor.getReferences() != null ? assessor.getReferences().stream()
                .map(AssessorReferenceDto::new)
                .collect(Collectors.toList()) : List.of();

        this.preferenceDtoList = assessor.getPreferences() != null ? assessor.getPreferences().stream()
                .map(AssessorPreferenceDto::new)
                .collect(Collectors.toList()) : List.of();

        this.certificateList = assessor.getCertificates() != null ? assessor.getCertificates().stream()
                .map(AssessorCertificateDto::new)
                .collect(Collectors.toList()) : List.of();
    }
}
