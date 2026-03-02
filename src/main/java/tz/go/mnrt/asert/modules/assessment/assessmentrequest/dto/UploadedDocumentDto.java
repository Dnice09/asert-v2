package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadedDocumentDto {

    @NotNull(message = "Attachment ID is required")
    @Positive(message = "Attachment ID must be positive")
    private Long attachmentId;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name must not exceed 255 characters")
    private String name;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotBlank(message = "Upload type is required")
    @Size(max = 100, message = "Upload type must not exceed 100 characters")
    private String uploadType;

    @NotBlank(message = "File type is required")
    @Size(max = 100, message = "File type must not exceed 100 characters")
    private String fileType;
}