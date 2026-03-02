package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssessmentRequestDocumentResponseDto {
    private Long id; // FileUpload ID
    private UUID uuid; // FileUpload UUID
    
    private String description;
    private String uploadType;
    
    private Long mediaId; // Same as id, for consistency with hotel media
    private String mediaUrl;
    private String mediaFileName;
    private String mediaFileType;
    private Long fileSize;
    
    private Long assessmentRequestId;
    private String assessmentRequestUuid;
    
    private Long userId;
    private String userName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public AssessmentRequestDocumentResponseDto(FileUpload fileUpload, String uploadType, String description) {
        this.id = fileUpload.getId();
        this.uuid = fileUpload.getUuid();
        this.description = description;
        this.uploadType = uploadType;
        
        // Set media fields for consistency with hotel media response
        this.mediaId = fileUpload.getId();
        this.mediaFileName = fileUpload.getName();
        this.mediaFileType = fileUpload.getFileType();
        this.fileSize = fileUpload.getFileSize();
        this.mediaUrl = "/api/v1/uploads/" + fileUpload.getUuid() + "/view";
        
        this.userId = fileUpload.getUserId();
        this.createdAt = fileUpload.getCreatedAt();
    }
}