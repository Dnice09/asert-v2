package tz.go.mnrt.asert.modules.assessment.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.document.entity.AssessorDocument;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AssessorDocumentDto implements Serializable {

    private Long id;
    private UUID uuid;
    private String title;
    private String fileType;
    private String filePath;
    private Boolean verified;
    private Long documentTypeId;
    private String documentTypeName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadedAt;
    private Boolean isCv;

    public AssessorDocumentDto(AssessorDocument entity, String uploadedFilesUrl) {
        entity.toDao(this);

        this.documentTypeId = entity.getDocumentTypeId();
        this.documentTypeName = entity.getDocumentType().getName();
        this.isCv = documentTypeName.equalsIgnoreCase("CV") || documentTypeName.equalsIgnoreCase("Curriculum Vitae");

        // Return file download URL instead of base64 data for performance
        if (entity.getUuid() != null) {
            this.filePath = "/api/v1/uploads/" + entity.getUuid() + "/view";
        }
    }
}
