package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.EssentialItemEvidence;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItemEvidenceResponseDto {

    private Long id;
    private UUID uuid;
    private String evidenceDescription;
    private String evidenceType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileSizeFormatted;
    private String mimeType;
    private String displayName;
    private Boolean isImage;
    private Boolean isDocument;
    private Boolean isVideo;
    private UUID fileUploadId;

    public EssentialItemEvidenceResponseDto(EssentialItemEvidence entity) {
        BeanUtils.copyProperties(entity, this);
        
        this.fileSizeFormatted = entity.getFileSizeFormatted();
        this.displayName = entity.getDisplayName();
        this.isImage = entity.isImage();
        this.isDocument = entity.isDocument();
        this.isVideo = entity.isVideo();
        
        if (entity.getFileUpload() != null) {
            this.fileUploadId = entity.getFileUpload().getUuid();
            this.fileUrl = "/api/v1/uploads/" + entity.getFileUpload().getUuid() + "/view";
        }
    }
}